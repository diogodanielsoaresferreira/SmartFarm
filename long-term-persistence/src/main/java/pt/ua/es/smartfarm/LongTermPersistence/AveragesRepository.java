package pt.ua.es.smartfarm.LongTermPersistence;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Repository of the averages sent by the sensors.
 */

@CrossOrigin
public interface AveragesRepository extends MongoRepository<AverageMessage, String> {
    
    /**
     * Query the repository by the ID of a sensor.
     * @param sensorId ID of the sensor to fetch the messages from
     * @return list of all the messages of that sensor
     */
    public List<AverageMessage> findBySensorId(String sensorId);
    
}
