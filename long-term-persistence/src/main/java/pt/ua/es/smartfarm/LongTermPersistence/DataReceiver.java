
package pt.ua.es.smartfarm.LongTermPersistence;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;


/**
 * Receives the data from the message bus and saves into the messageRepository.
 */
@RestController
@Slf4j
public class DataReceiver {
    
    @Autowired
    private KafkaTemplate<String, String> template;
    
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AveragesRepository averagesRepository;
    
    /**
     * Listen for messages on the kafka broker and saves it into the messageRepository.
     * @param cr Consumer record fetched from the messge bus
     */
    @KafkaListener(topics = "sensor_data")
    public void newData(ConsumerRecord<?, ?> cr){
        log.info("Message received from {}: {} .", cr.key(), cr.value());
        
        JSONParser parser = new JSONParser();
        
        try {
            JSONObject jsonMessage = (JSONObject) parser.parse(cr.value().toString());
            SensorMessage message = new SensorMessage(
                    cr.key().toString(),
                    (String) jsonMessage.get("value"),
                    (String) jsonMessage.get("timestamp"),
                    (String) jsonMessage.get("status")
            );
            messageRepository.save(message);
            log.info("Message {} from sensor {} saved into the messageRepository.", cr.value(), cr.key());
            
        } catch (ParseException ex) {
            log.error("Could not parse the message received from the sensor " +
                    cr.key()+": "+cr.value());
        }
                
    }

    @KafkaListener(topics = "average_sensor_data")
    public void test(ConsumerRecord<?, ?> cr){

        log.info("Average Calculated for " + cr.key() + ":" + cr.value());

        AverageMessage message = new AverageMessage(
                cr.key().toString(),
                cr.value().toString()
        );
        averagesRepository.save(message);
        log.info("Average {} from sensor {} saved into the averagesRepository.", cr.value(), cr.key());

    }
}
