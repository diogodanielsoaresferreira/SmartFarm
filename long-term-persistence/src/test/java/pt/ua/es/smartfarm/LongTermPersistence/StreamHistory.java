package pt.ua.es.smartfarm.LongTermPersistence;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class StreamHistory extends CucumberRoot {
    
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
    
    @Given("^a sensor (.+)$")
    public void newSensor(int sensorId) throws Throwable{
        this.sensorId = sensorId;
    }
    
    @When("^the sensor sends the values value1: (.+), value2: (.+), value3: (.+) to the platform$")
    public void sensorSentValue(String data1, String data2, String data3) throws Throwable{
        Collection<String> col = new ArrayList();
        col.add("sensor_data");
        consumer.subscribe(col);
        
        kafkaTemplate.send("sensor_data", String.valueOf(sensorId), data1);
        kafkaTemplate.send("sensor_data", String.valueOf(sensorId), data2);
        kafkaTemplate.send("sensor_data", String.valueOf(sensorId), data3);
        kafkaTemplate.flush();
        
    }
    
    @Then("^the value1: (.+), value2: (.+), value3: (.+) are available on the platform$")
    public void assertValuesAreStored(String data1, String data2, String data3) throws Throwable, ParseException{
        
        boolean receive1 = false, receive2 = false, receive3 = false;
        // Wait to receive and store values
        Thread.sleep(1000);
        
        JSONParser parser = new JSONParser();
        JSONObject jsonMessage = (JSONObject) parser.parse(data1);
        JSONObject jsonMessage2 = (JSONObject) parser.parse(data2);
        JSONObject jsonMessage3 = (JSONObject) parser.parse(data3);
        
        String value1 = (String) jsonMessage.get("value");
        String timestamp1 = (String) jsonMessage.get("timestamp");
        String status1 = (String) jsonMessage.get("status");
        
        String value2 = (String) jsonMessage2.get("value");
        String timestamp2 = (String) jsonMessage2.get("timestamp");
        String status2 = (String) jsonMessage2.get("status");
        
        String value3 = (String) jsonMessage3.get("value");
        String timestamp3 = (String) jsonMessage3.get("timestamp");
        String status3 = (String) jsonMessage3.get("status");
        
        List<SensorMessage> messages = messRepo.findBySensorId(String.valueOf(this.sensorId));
        for (SensorMessage message : messages){
            System.out.println(message.getValue());
            System.out.println(value1);
            System.out.println(message.getTimestamp());
            System.out.println(timestamp1);
            System.out.println(message.getStatus());
            System.out.println(status1);
            if(message.getStatus().equals(status1) && message.getValue().equals(value1) && message.getTimestamp().equals(timestamp1)){
                receive1 = true;
            }
            
            if(message.getStatus().equals(status2) && message.getValue().equals(value2) && message.getTimestamp().equals(timestamp2)){
                receive2 = true;
            }
            
            if(message.getStatus().equals(status3) && message.getValue().equals(value3) && message.getTimestamp().equals(timestamp3)){
                receive3 = true;
            }
            
            if(receive1 && receive2 && receive3)
                break;
        }
        
        assertThat(String.valueOf(receive1), is("true"));
        assertThat(String.valueOf(receive2), is("true"));
        assertThat(String.valueOf(receive3), is("true"));
    }
}
