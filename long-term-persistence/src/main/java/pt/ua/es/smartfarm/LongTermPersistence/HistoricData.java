
package pt.ua.es.smartfarm.LongTermPersistence;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest API to fetch the data from the sensors.
 */
@RestController
@Slf4j
public class HistoricData {
    
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AveragesRepository averagesRepository;
    
    /**
     * Fetch all the data from a sensor.
     * @param sensor_id ID of the sensor to fetch data from
     * @return list of data from a sensor
     */
    @GetMapping("/historic/{sensor_id}")
    @CrossOrigin
    public List<SensorMessage> retrieveAllSensorMessages(@PathVariable String sensor_id){
        log.info("Retrieve historic data from sensor {} .", sensor_id);
        return messageRepository.findBySensorId(sensor_id);
    }

    /**
     * Fetch all the averages data from a sensor.
     * @param sensor_id ID of the sensor to fetch data from
     * @return list of averages from a sensor
     */
    @GetMapping("/averages/{sensor_id}")
    @CrossOrigin
    public List<AverageMessage> retrieveAllAverageMessages(@PathVariable String sensor_id){
        log.info("Retrieve averages data from sensor {} .", sensor_id);
        return averagesRepository.findBySensorId(sensor_id);
    }
    
}
