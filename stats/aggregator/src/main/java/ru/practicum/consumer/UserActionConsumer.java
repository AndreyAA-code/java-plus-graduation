package ru.practicum.consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;
import ru.practicum.stats.common.config.KafkaClient;
import ru.practicum.stats.common.config.KafkaTopicsProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionConsumer {
    private final KafkaClient kafkaClient;
    private final KafkaTopicsProperties topicsProperties;
    
    @Getter
    private Consumer<Long, SpecificRecordBase> consumer;

    @PostConstruct
    public void init() {
        consumer = kafkaClient.getConsumerAction();
        log.info("UserActionConsumer initialized");
    }

    public void subscribe() {
        consumer.subscribe(List.of(topicsProperties.getUserActions()));
        log.info("Подписка на топик: {}", topicsProperties.getUserActions());
    }

    public ConsumerRecords<Long, SpecificRecordBase> poll(Duration timeout) {
        return consumer.poll(timeout);
    }

    public void commitSync(Map<TopicPartition, OffsetAndMetadata> offsets) {
        consumer.commitSync(offsets);
    }

    public void commitAsync(Map<TopicPartition, OffsetAndMetadata> offsets, OffsetCommitCallback callback) {
        consumer.commitAsync(offsets, callback);
    }

    public void wakeup() {
        if (consumer != null) {
            consumer.wakeup();
        }
    }

    @PreDestroy
    public void close() {
        if (consumer != null) {
            try {
                consumer.wakeup();
                consumer.close(Duration.ofSeconds(5));
                log.info("UserActionConsumer closed");
            } catch (Exception e) {
                log.error("Error closing consumer", e);
            }
        }
    }
}
