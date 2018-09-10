
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
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class ReadSensorData extends CucumberRoot {
    
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
    public void givenSensor(int sensorId) throws Throwable{
        this.sensorId = sensorId;
    }
    
    @When("^the sensor sends the data (.+) to the platform$")
    public void sensorSendData(String data) throws Throwable{
        
        Collection<String> col = new ArrayList();
        col.add("sensors_data");
        consumer.subscribe(col);
        
        kafkaTemplate.send("sensors_data", String.valueOf(sensorId), data);
        kafkaTemplate.flush();
    }
    
    
    @When("^the sensor sends the values to the platform: value1: (.+), value2: (.+), value3: (.+)$")
    public void sensorSendData(String data1, String data2, String data3) throws Throwable{
        
        Collection<String> col = new ArrayList();
        col.add("sensors_data");
        consumer.subscribe(col);
        
        kafkaTemplate.send("sensors_data", String.valueOf(sensorId), data1);
        kafkaTemplate.send("sensors_data", String.valueOf(sensorId), data2);
        kafkaTemplate.send("sensors_data", String.valueOf(sensorId), data3);
        kafkaTemplate.flush();
    }
    
    @Then("^the (.+) is available on the platform$")
    public void assertDataInPlatform(String data) throws Throwable{
        boolean received = false;
        
        while(!received){
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records){
                received = true;
                assertThat(data, is(record.value()));
                assertThat(String.valueOf(this.sensorId), is(record.key()));
            }
        }
    }
    
    @Then("^the value1: (.+), value2: (.+), value3: (.+) are available on the platform$")
    public void assertDataInPlatform(String data1, String data2, String data3) throws Throwable{
        int received = 0;
        
        while(received<3){
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records){
                
                received += 1;
                assertThat(record.key(), is(String.valueOf(sensorId)));
                switch (received) {
                    case 1:
                        assertThat(record.value(), is(data1));
                        break;
                    case 2:
                        assertThat(record.value(), is(data2));
                        break;
                    default:
                        assertThat(record.value(), is(data3));
                        break;
                }
            }
        }
    }
    
}
