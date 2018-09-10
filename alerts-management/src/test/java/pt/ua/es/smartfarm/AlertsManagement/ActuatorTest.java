package pt.ua.es.smartfarm.AlertsManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActuatorTest extends SpringBootEmbeddedKafka {
    
    Alerts alertTest;
    
    Actuators actuatorTest;
    
    @Autowired
    AlertsRepository alertsRepo;
    
    @Autowired
    ActuatorsRepository actRepo;
    
    static KafkaConsumer<String, String> consumer;
    
    @BeforeClass
    public static void setupKafkaConsumer(){
        System.setProperty("kafka_bootstrap_servers", "127.0.0.1:9092");
        
        String kafka_bootstrap_servers = System.getProperty("kafka_bootstrap_servers");
        
        if(kafka_bootstrap_servers==null){
            kafka_bootstrap_servers = System.getenv("kafka_bootstrap_servers");
        }
        
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", kafka_bootstrap_servers);
        props.put("group.id", "consumer-test1");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        consumer = new KafkaConsumer<>(props);
    }
    
    @AfterClass
    public static void closeKafkaConsumer(){
        consumer.close();
    }
    
    
    @Test
    public void actuator_scenario1() throws Exception{
        String alertType = "min";
        String threshold = "500";
        String sensorId = "500";
        String actuatorId = "504";
        String data = "{\"value\": \"14\", \"timestamp\": \"2018-05-05T22:54:00.839222\", \"status\": \"ok\"}";
        String value = "27";
        given_alert_and_actuator(alertType, threshold, sensorId, actuatorId, value);
        sensor_sends_data(data);
        actuator_received_message(actuatorId, value);
    }
    
    @Test
    public void actuator_scenario2() throws Exception{
        String alertType = "max";
        String threshold = "1";
        String sensorId = "501";
        String actuatorId = "505";
        String data = "{\"value\": \"14\", \"timestamp\": \"2018-05-05T22:54:00.839222\", \"status\": \"ok\"}";
        String value = "28";
        given_alert_and_actuator(alertType, threshold, sensorId, actuatorId, value);
        sensor_sends_data(data);
        actuator_received_message(actuatorId, value);
    }
    
    @Test
    public void actuator_scenario3() throws Exception{
        String alertType = "averageMax";
        String threshold = "-50";
        String sensorId = "502";
        String actuatorId = "506";
        String data = "{\"value\": \"14\", \"timestamp\": \"2018-05-05T22:54:00.839222\", \"status\": \"ok\"}";
        String value = "30";
        given_alert_and_actuator(alertType, threshold, sensorId, actuatorId, value);
        sensor_sends_data(data);
        actuator_received_message(actuatorId, value);
    }
    
    @Test
    public void actuator_scenario4() throws Exception{
        String alertType = "averageMin";
        String threshold = "-50";
        String sensorId = "503";
        String actuatorId = "507";
        String data = "{\"value\": \"14\", \"timestamp\": \"2018-05-05T22:54:00.839222\", \"status\": \"ok\"}";
        String value = "31";
        given_alert_and_actuator(alertType, threshold, sensorId, actuatorId, value);
        sensor_sends_data(data);
        actuator_received_message(actuatorId, value);
    }
    
    
    public void given_alert_and_actuator(String alertType, String threshold, String sensorId, String actuatorId, String value) throws Exception{
        
        alertTest = new Alerts();
        alertTest.setAlertType(alertType);
        alertTest.setThreshold(threshold);
        
        ArrayList<String> sensorList = new ArrayList<>();
        sensorList.add(sensorId);
        alertTest.setSensors(sensorList);
        
        alertsRepo.save(alertTest);
        
        actuatorTest = new Actuators();
        
        actuatorTest.setAlertId(String.valueOf(alertTest.getId()));
        sensorList = new ArrayList<>();
        sensorList.add(actuatorId);
        actuatorTest.setSensors(sensorList);
        actuatorTest.setValueToBeSent(value);
        actRepo.save(actuatorTest);
        
    }
    
    public void sensor_sends_data(String data) throws Exception{
        
        Collection<String> col = new ArrayList();
        col.add("actuator_value");
        consumer.subscribe(col);
        
        consumer.poll(100);
        
        template.send("sensor_data", String.valueOf(alertTest.getSensors().get(0)), data);
        template.flush();
    }
    
    public void actuator_received_message (String actuatorId, String value) throws Exception{
        boolean received = false;
        
        // Wait for the processing of the alerts
        Thread.sleep(2000);
        
        ConsumerRecords<String, String> records = consumer.poll(20000);
        
        for (ConsumerRecord<String, String> record : records){
            System.out.println(record.key()+":"+record.value());
            if(actuatorTest.getSensors().stream().anyMatch(sensor -> sensor.equals(record.key())) && record.value().equals(actuatorTest.getValueToBeSent())){
                received = true;
                break;
            }
        }
        actRepo.deleteById(actuatorTest.getId());
        alertsRepo.deleteById(alertTest.getId());
        assertThat(String.valueOf(received), is("true"));

    }
    
}
