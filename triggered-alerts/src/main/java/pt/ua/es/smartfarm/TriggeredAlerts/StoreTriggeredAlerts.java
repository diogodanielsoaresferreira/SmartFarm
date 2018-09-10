package pt.ua.es.smartfarm.TriggeredAlerts;

import java.util.List;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class StoreTriggeredAlerts {
    
    @Autowired
    private SimpMessagingTemplate simpTemplate;
    
    @Autowired
    private TriggeredAlertsRepository repository;
    
    @KafkaListener(topics = "triggered_alerts")
    public void receiveTriggeredAlert(ConsumerRecord<?, ?> cr) {
        log.info("Triggered alert from {}: {} .", cr.key(), cr.value());
        JSONParser parser = new JSONParser();
        JSONObject jsonMessage;
        try {
            jsonMessage = (JSONObject) parser.parse(cr.value().toString());
            
            // Create one trigger to all triggered alerts
            List<String> alertsId = (ArrayList<String>)jsonMessage.get("alertsId");
            for(String alert : alertsId) {
                TriggeredAlerts tAlert = new TriggeredAlerts();
                tAlert.setValueSensor(jsonMessage.get("value").toString());
                tAlert.setAlertId(Long.parseLong(alert));
                repository.save(tAlert);
                
                log.info("Triggered alert saved");
                
                // Create new json to this specific triggered alert
                JSONObject triggered = new JSONObject();
                triggered.put("alertId", Long.parseLong(alert));
                triggered.put("value", jsonMessage.get("value").toString());
                triggered.put("dateCreated", jsonMessage.get("timestamp").toString());

                // Send triggered alert to the websocket
                this.simpTemplate.convertAndSend("/topic/alerts", triggered.toString());
            }
            log.info("Triggered alerts sent to the websocket");
        } catch (ParseException ex) {
           log.error("Could not save triggered alert: {}", ex.toString());
        }
    }
}
