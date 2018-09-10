package pt.ua.es.smartfarm.ServiceLayer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SendDataToSensor {
    
    @Autowired
    KafkaTemplate<String, String> template;
    
    @PostMapping("/actuate/{sensor_id}")
    @CrossOrigin
    public void sendValueToActuator(@PathVariable String sensor_id, @RequestBody String value){
        template.send("actuator_value", sensor_id, value);
    }
    
}
