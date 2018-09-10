
package pt.ua.es.smartfarm.ServiceLayer;

import static org.hamcrest.MatcherAssert.assertThat;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.hamcrest.Matchers.is;


public class SensorLocation extends CucumberRoot {
    
    @Given("^a new sensor with latitude (.+) and longitude (.+)$")
    public void newSensor(double latitude, double longitude) throws Throwable{
        farmTest = new Farms();
        farmTest.setAddress("AddressTest1");
        farmTest.setArea(1.0);
        farmTest.setLatitude(40.0);
        farmTest.setLongitude(-8.0);
        farmTest.setName("FarmTest1");
        farmsRepo.save(farmTest);
        
        sensorTest = new Sensors();
        sensorTest.setName("SensorTest1");
        sensorTest.setModel("ModelTest1");
        sensorTest.setType("typeTest1");
        sensorTest.setStatus("statusTest1");
        sensorTest.setActuator(true);
        sensorTest.setLongitude(longitude);
        sensorTest.setLatitude(latitude);
        sensorTest.setFarm(farmTest.getId());
        sensorsRepo.save(sensorTest);
    }
    
    @When("^the user checks the sensor description$")
    public void getSensorDescription() throws Throwable{
        sensorQuery = sensorsRepo.findById(sensorTest.getId()).get();
    }
    
    @Then("^the sensor latitude and longitude should be (.+) and (.+), respectively$")
    public void assertLatitudeAndLongitude(double latitude, double longitude) throws Throwable{
        assertThat(latitude, is(sensorQuery.getLatitude()));
        assertThat(longitude, is(sensorQuery.getLongitude()));
        farmsRepo.delete(farmTest);
        sensorsRepo.delete(sensorTest);
    }
    
}
