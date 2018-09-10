
package pt.ua.es.smartfarm.ServiceLayer;

import cucumber.api.java.en.Given;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = ServiceLayerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
public class CucumberRoot {
    @Autowired
    FarmsRepository farmsRepo;
    
    @Autowired
    SensorsRepository sensorsRepo;
    
    Farms farmTest;
    
    Sensors sensorTest;
    
    Sensors sensorQuery;
    
    int sensorId;
    
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    
    KafkaConsumer<String, String> consumer;
    
    
}
