package ru.practicum.stats.common.serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {
    
    private final EncoderFactory encoderFactory = EncoderFactory.get();
    private BinaryEncoder encoder;
    
    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            return null;
        }
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            DatumWriter<T> writer = new SpecificDatumWriter<>(data.getSchema());
            encoder = encoderFactory.binaryEncoder(out, encoder);
            writer.write(data, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (IOException ex) {
            throw new SerializationException("Error serializing Avro message for topic " + topic, ex);
        }
    }
}
