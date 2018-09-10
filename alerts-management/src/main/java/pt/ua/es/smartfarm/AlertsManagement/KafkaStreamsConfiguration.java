package pt.ua.es.smartfarm.AlertsManagement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
@EnableKafkaStreams
@Slf4j
public class KafkaStreamsConfiguration {

    @Autowired
    private AlertsRepository repository;
    
    @Autowired
    private ActuatorsRepository actRepository;
    
    @Autowired
    private KafkaTemplate<String, String> template;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfigs() {
        String kafka_bootstrap_servers = System.getProperty("kafka_bootstrap_servers");
        
        if(kafka_bootstrap_servers==null){
            kafka_bootstrap_servers = System.getenv("kafka_bootstrap_servers");
        }
        
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-alerts");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_bootstrap_servers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        return new StreamsConfig(props);
    }

    @Bean
    public KStream<String, String> kStream(StreamsBuilder kStreamBuilder) {
        KStream<String, String> stream = kStreamBuilder.stream("sensor_data");

        // Aggregate, count and sum all values received from the sensors
        KTable<String,AvgValue> countAndSum = stream
                .groupByKey()
                .aggregate(
                        () -> new AvgValue(0, 0D),
                        (final String key, final String value, final AvgValue aggregate) -> {
                            AvgValue newavg = aggregate.add(key, getDoubleValue(value), aggregate);
                            return newavg;
                        },
                    new AvgValueSerde()
                );

        // With the count and sum, calculate the average value and send it to a queue
        KTable<String, String> avg = countAndSum.mapValues((v) -> Double.toString(v.getSum() / v.getCount()));

        avg.toStream().to("average_sensor_data");

        // Find minimum, maximum and averages alerts
        stream
            // Get the average value from that sensor
            .join(avg, (newValue, average) -> {
                return new InternalClass(newValue, average);
            })
            // Find all alerts and change the format of message to add the alerts
            .flatMap((String key, InternalClass values) -> {
                List<KeyValue<String, InternalClass>> result = new LinkedList<>();
                
                // Get a list of all alerts triggered
                List<String> alerts = repository.findBySensorsIn(key).stream()
                        .filter(alert ->
                                ("min".equals(alert.getAlertType()) &&
                                        Double.parseDouble(alert.getThreshold()) > getDoubleValue(values.value1))
                                        ||
                                        ("max".equals(alert.getAlertType()) &&
                                                Double.parseDouble(alert.getThreshold()) < getDoubleValue(values.value1))
                                        ||
                                        ("averageMax".equals(alert.getAlertType()) &&
                                                Double.parseDouble(values.value2) + Double.parseDouble(alert.getThreshold())
                                                        < getDoubleValue(values.value1))
                                        ||
                                        ("averageMin".equals(alert.getAlertType()) &&
                                                Double.parseDouble(values.value2) - Double.parseDouble(alert.getThreshold())
                                                        > getDoubleValue(values.value1))
                        )
                        .map(e->e.getId().toString())
                        .collect(Collectors.toList());
                
                JSONParser parser = new JSONParser();
                JSONObject jsonMessage;
                try {
                    jsonMessage = (JSONObject) parser.parse(values.value1);
                    
                    // Create list with alerts id that will be triggered
                    JSONArray alertsId = new JSONArray();
                    
                    alertsId.addAll(alerts);
                    jsonMessage.put("alertsId", alertsId);

                    values.setValue1(jsonMessage.toString());
                    result.add(KeyValue.pair(key, values));
                } catch (ParseException ex) {
                   log.error("Problem parsing alert: "+values.value1);
                }
                return result;
            })
            .mapValues(values -> (String)values.value1)
            .filter((key, value) -> getAllAlerts(value).size()>0)
            .to("triggered_alerts");
        
        
        // Get all the status from the sensors
        KStream<String, String> sensorStatusStream = 
            kStreamBuilder.stream("sensor_status");
        KTable<String, String> sensorStatusTable = 
            sensorStatusStream.groupByKey().reduce((aggregatedValue, newValue) -> newValue);
        
        // Compare last status and new sensor status
        // and put new status to topic if it is different from last status
        stream
            .mapValues((value) -> getSensorStatus(value))
            .leftJoin(sensorStatusTable, (String newValue, String status) -> new InternalClass(newValue, status))
            .filter((String key, InternalClass status) -> !status.value1.equals(status.value2))
            .mapValues((InternalClass status) -> status.value1)
            .to("sensor_status");

        // Send values to actuators when an alert is triggered
        kStreamBuilder.stream("triggered_alerts")
            .foreach((key, value) -> {
                
                JSONArray alerts = getAllAlerts((String)value);

                // For all alerts triggered
                alerts.forEach((alert) -> {
                    
                    // Find all actuators for the alert
                    Actuators actuator = actRepository.findByAlertId(alert.toString());
                    
                    if (actuator != null) {
                        
                        // Get all sensor to send the information
                        actuator.getSensors().forEach((sensor) -> {
                            
                            // Send the pre-defined information in the actuator
                            template.send("actuator_value", sensor, actuator.getValueToBeSent());
                        });
                    }
                });
                
            });
        
        return stream;
    }

    private double getDoubleValue(String message){
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonMessage = (JSONObject) parser.parse(message);
            return Double.parseDouble((String) jsonMessage.get("value"));
        } catch (Exception ex) {
            log.error("Error parsing value from message: "+message);
            return 0;
        }
    }
    
    private String getSensorStatus(String message){
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonMessage = (JSONObject) parser.parse(message);
            return (String) jsonMessage.get("status");
        } catch (Exception ex) {
            log.error("Error parsing the status from message: "+message);
            return "";
        }
    }
    
    private JSONArray getAllAlerts(String message){
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonMessage = (JSONObject) parser.parse(message);
            return (JSONArray) jsonMessage.get("alertsId");
        } catch (Exception ex) {
            log.error("Error parsing the list of alerts in an alert: "+message);
            return null;
        }
    }

    @KafkaListener(topics = "average_sensor_data")
    public void newAverageCalculated(ConsumerRecord<?, ?> cr){
        log.info("Average Calculated for "+cr.key()+":"+cr.value());
    }
    
    @KafkaListener(topics = "sensor_status")
    public void newSensorStatus(ConsumerRecord<?, ?> cr){
        log.info("Sensor "+cr.key()+" with new status "+cr.value());
    }
    
    @KafkaListener(topics = "triggered_alerts")
    public void newTriggeredAlert(ConsumerRecord<?, ?> cr){
        log.info("New triggered alert: "+cr.key()+":"+cr.value());
    }
    
    @KafkaListener(topics = "actuator_value")
    public void newActuatorValue(ConsumerRecord<?, ?> cr){
        log.info("Value sent to actuator: "+cr.key()+":"+cr.value());
    }

    class InternalClass{
        String value1;
        String value2;

        public InternalClass(String arg1, String arg2){
            value1 = arg1;
            value2 = arg2;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }
    }
}
