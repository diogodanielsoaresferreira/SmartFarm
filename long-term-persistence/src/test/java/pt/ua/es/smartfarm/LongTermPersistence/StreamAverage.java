package pt.ua.es.smartfarm.LongTermPersistence;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class StreamAverage extends CucumberRoot {
    
    int sensorId;
    
    @Before
    public void setupKafkaConsumer(){
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
    
    @After
    public void closeKafkaConsumer(){
        consumer.close();
    }
    
    @Given("^a new sensor (.+)$")
    public void newSensor(int sensorId) throws Throwable{
        this.sensorId = sensorId;
    }
    
    @When("^the sensor sends values value1: (.+), value2: (.+), value3: (.+) to the platform$")
    public void sensorSentValue(String data1, String data2, String data3) throws Throwable{
        Collection<String> col = new ArrayList();
        col.add("sensor_data");
        consumer.subscribe(col);
        
        kafkaTemplate.send("sensor_data", String.valueOf(sensorId), data1);
        kafkaTemplate.send("sensor_data", String.valueOf(sensorId), data2);
        kafkaTemplate.send("sensor_data", String.valueOf(sensorId), data3);
        kafkaTemplate.flush();
    }
    
    @Then("^the average value of the sensor should be (.+)$")
    public void assertSensorAverage(String average) throws Throwable{
        // Wait to receive average update
        Thread.sleep(30000);
        String saverage;
        try{
            saverage = averRepo.findBySensorId(String.valueOf(this.sensorId)).get(0).getValue();
        }
        catch(Exception e){
            saverage = "0";
        }
        
        assertThat(saverage, is(average));
    }
    
}
