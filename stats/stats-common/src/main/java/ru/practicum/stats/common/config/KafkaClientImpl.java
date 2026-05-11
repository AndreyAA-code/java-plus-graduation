package ru.practicum.stats.common.config;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.deserializer.UserActionAvroDeserializer;
import ru.practicum.serializer.AvroSerializer;
import ru.practicum.deserializer.EventSimilarityAvroDeserializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaClientImpl implements KafkaClient {
    
    private final KafkaTopicsProperties topicsProperties;
    private Producer<Long, SpecificRecordBase> producer;
    private Consumer<Long, SpecificRecordBase> consumerAction;
    private Consumer<Long, SpecificRecordBase> consumerSimilarity;
    
    @Value("${kafka.bootstrap.servers:localhost:9092}")
    private String bootstrapServers;
    
    @Override
    public Producer<Long, SpecificRecordBase> getProducer() {
        if (producer == null) {
            Map<String, Object> config = new HashMap<>();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);
            producer = new KafkaProducer<>(config);
        }
        return producer;
    }
    
    @Override
    public Consumer<Long, SpecificRecordBase> getConsumerAction() {
        if (consumerAction == null) {
            Map<String, Object> config = new HashMap<>();
            config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
            config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionAvroDeserializer.class);
            config.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-group");
            config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            consumerAction = new KafkaConsumer<>(config);
        }
        return consumerAction;
    }
    
    @Override
    public Consumer<Long, SpecificRecordBase> getConsumerSimilarity() {
        if (consumerSimilarity == null) {
            Map<String, Object> config = new HashMap<>();
            config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
            config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventSimilarityAvroDeserializer.class);
            config.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-similarity-group-new");
            config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            consumerSimilarity = new KafkaConsumer<>(config);
        }
        return consumerSimilarity;
    }
    
    @Override
    public Duration getPollTimeout() {
        return Duration.ofMillis(1000);
    }
    
    @Override
    public KafkaTopicsProperties getTopicsProperties() {
        return topicsProperties;
    }
}
