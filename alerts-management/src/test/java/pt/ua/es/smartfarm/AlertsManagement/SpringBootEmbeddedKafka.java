package pt.ua.es.smartfarm.AlertsManagement;

import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

/*
This is an duplicated way to configure an embedded kafka cause maven and the IDE is running them different
Feel free to optimize
 */

@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
        topics= {"sensor_data", "average_sensor_data", "triggered_alerts", "actuator_value", "sensor_status"})
public abstract class SpringBootEmbeddedKafka {

    @Autowired
    public KafkaTemplate<String, String> template;


    @BeforeClass
    public static void setUpClass() {
        System.setProperty("kafka_bootstrap_servers", "127.0.0.1:9092");
    }
    
}
