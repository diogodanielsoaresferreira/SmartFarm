
package pt.ua.es.smartfarm.AlertsManagement;

import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;


public class AvgValueSerde implements Serde<AvgValue> {

    @Override
    public void configure(Map<String, ?> map, boolean bln) {
    }

    @Override
    public void close() {
    }

    @Override
    public Serializer<AvgValue> serializer() {
        return new AvgValueSerializer();
    }

    @Override
    public Deserializer<AvgValue> deserializer() {
        return new AvgValueDeserializer();
    }

    
}
