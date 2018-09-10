package pt.ua.es.smartfarm.AlertsManagement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by gwen on 1/12/16.
 */
public class AvgValueDeserializer implements Deserializer<AvgValue> {
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    public AvgValue deserialize(String topic, byte[] data) {
        try {
            if (data == null)
                return null;

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = new ObjectInputStream(bis);
        AvgValue avg = (AvgValue) in.readObject();
        in.close();
        return avg;
        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
        }
        return null;
    }

    public void close() {

    }
}
