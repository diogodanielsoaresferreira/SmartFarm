
package pt.ua.es.smartfarm.ServiceLayer;

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


public class ReadSensorState extends CucumberRoot {
    
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
    
    @Given("^a new sensor with state (.+)$")
    public void newSensor(String sensorState) throws Throwable{
        farmTest = new Farms();
        farmTest.setAddress("AddressTest2");
        farmTest.setArea(1.0);
        farmTest.setLatitude(40.0);
        farmTest.setLongitude(-8.0);
        farmTest.setName("FarmTest2");
        farmsRepo.save(farmTest);
        
        sensorTest = new Sensors();
        sensorTest.setName("SensorTest2");
        sensorTest.setModel("ModelTest2");
        sensorTest.setType("typeTest2");
        sensorTest.setStatus("statusTest2");
        sensorTest.setActuator(true);
        sensorTest.setLongitude(40.0);
        sensorTest.setStatus(sensorState);
        sensorTest.setLatitude(-8.0);
        sensorTest.setFarm(farmTest.getId());
        sensorsRepo.save(sensorTest);
    }
    
    @When("^the sensor sends a message (.+) to the platform$")
    public void sensorSendMessage(String message) throws Throwable{
        
        Collection<String> col = new ArrayList();
        col.add("sensor_data");
        consumer.subscribe(col);
        
        kafkaTemplate.send("sensor_data", String.valueOf(sensorTest.getId()), message);
        kafkaTemplate.flush();
    }
    
    @Then("^the sensor state should be (.+)$")
    public void assertSensorChangedState(String state) throws Throwable{
        // Wait to receive state update
        Thread.sleep(1000);
        assertThat(sensorsRepo.findById(sensorTest.getId()).get().getStatus(), is(state));
        sensorsRepo.delete(sensorTest);
        farmsRepo.delete(farmTest);
    }
    
}
