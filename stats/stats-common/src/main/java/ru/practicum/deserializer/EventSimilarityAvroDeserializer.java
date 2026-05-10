package ru.practicum.deserializer;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.Map;

public class EventSimilarityAvroDeserializer implements Deserializer<EventSimilarityAvro> {
    
    private final DatumReader<EventSimilarityAvro> reader = new SpecificDatumReader<>(EventSimilarityAvro.getClassSchema());
    private DecoderFactory decoderFactory = DecoderFactory.get();
    
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Конфигурация не требуется
    }
    
    @Override
    public EventSimilarityAvro deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        
        try {
            Decoder decoder = decoderFactory.binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing EventSimilarityAvro", e);
        }
    }
    
    @Override
    public void close() {
        // Ничего не нужно закрывать
    }
}
