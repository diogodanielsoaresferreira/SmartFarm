
package pt.ua.es.smartfarm.LongTermPersistence;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Message sent from a sensor.
 */
@NoArgsConstructor
@RequiredArgsConstructor
@Document(collection = "averageMessage")
@Getter
@Setter
public class AverageMessage implements Serializable {
    
    /**
     * Internal ID.
     */
    @Id
    private String objectId; 
    
    /**
     * ID of the sensor.
     */
    @NonNull
    private String sensorId;
    
    /**
     * Average value sent by Kafka.
     */
    @NonNull
    private String value;
}
