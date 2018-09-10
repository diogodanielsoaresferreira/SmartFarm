package pt.ua.es.smartfarm.AlertsManagement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class AvgValueSerializer implements Serializer<AvgValue> {

    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    public byte[] serialize(String topic, AvgValue data) {
        try {
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(data);
            out.flush();
            byte[] buffer = bos.toByteArray();
            bos.close();
            return buffer;
        } catch (IOException ex) {
            return null;
        }
    }

    public void close() {
    }
}
