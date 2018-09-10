
package pt.ua.es.smartfarm.LongTermPersistence;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = LongTermPersistenceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
public class CucumberRoot {
    KafkaConsumer<String, String> consumer;
    
    
    
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    AveragesRepository averRepo;
    
    @Autowired
    MessageRepository messRepo;
    
}
