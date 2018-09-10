
package pt.ua.es.smartfarm.ServiceLayer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sends messages received from the sensors to the web socket.
 */
@RestController
@Slf4j
public class DataSender {
    
    @Autowired
    private SimpMessagingTemplate simpTemplate;
    
    
    /**
     * When a message is posted on the "sensor_data" topic,
     * this method is triggered and the value is sent to the web socket.
     * @param cr Consumer record of the data retrieved from the message bus
     */
    @CrossOrigin(origins="http://localhost:8080")
    @KafkaListener(topics = "sensor_data")
    public void listen(ConsumerRecord<?, ?> cr){
        log.info("Message received from {}: {} .", cr.key(), cr.value());
        
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonMessage = (JSONObject) parser.parse(cr.value().toString());

            // create response with sensor id, timestamp and readed value
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("id", cr.key());
            jsonResponse.put("timestamp", (String) jsonMessage.get("timestamp"));
            jsonResponse.put("value", (String) jsonMessage.get("value")); 
            
            this.simpTemplate.convertAndSend("/topic/sensors", jsonResponse.toString());
            log.info("Message sent to the websockets.");
        } catch (ParseException ex) {
            log.error("Could not parse the message received from the sensor " + cr.key()+": "+cr.value());
        }
    }
}
