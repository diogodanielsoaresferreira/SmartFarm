
package pt.ua.es.smartfarm.ServiceLayer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class ChangeSensorState {
    
    @Autowired
    private SensorsRepository sensorRepository;
    
    @KafkaListener(topics = "sensor_status")
    public void test2(ConsumerRecord<?, ?> cr){
        log.info("Sensor "+cr.key()+" with new status "+cr.value());
        
        try{
            long id = Long.parseLong((String)cr.key());
            Sensors sensor = sensorRepository.findById(id).get();
            sensor.setStatus((String)cr.value());
            sensorRepository.save(sensor);
            log.info("Status of sensor "+cr.key()+" changed for "+cr.value());
        }
        catch(Exception e){
            log.error("Could not save sensor "+cr.key()+" state: "+cr.value()+"; "+e.getMessage());
        }
    }
    
}
