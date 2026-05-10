package ru.practicum.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;
import ru.practicum.stats.common.config.KafkaClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionConsumer {
    private final KafkaClient kafkaClient;
    private Consumer<Long, SpecificRecordBase> consumer;

    public ConsumerRecords<Long, SpecificRecordBase> poll(Duration timeout) {
        return getConsumer().poll(timeout);
    }

    public void subscribe() {
        getConsumer().subscribe(List.of(kafkaClient.getTopicsProperties().getUserActions()));
        log.info("Подписка на топик: {}", kafkaClient.getTopicsProperties().getUserActions());
    }

    public void commitSync(Map<TopicPartition, OffsetAndMetadata> offsets) {
        try {
            getConsumer().commitSync(offsets);
            log.debug("Синхронный коммит оффсетов выполнен: {}", offsets);
        } catch (Exception e) {
            log.error("Ошибка синхронного коммита оффсетов", e);
            throw e;
        }
    }

    public void wakeup() {
        if (consumer != null) {
            log.info("Вызов wakeup для consumer");
            consumer.wakeup();
        }
    }

    public void close() {
        if (consumer != null) {
            try {
                consumer.wakeup();
                consumer.close(Duration.ofMillis(100));
                log.info("Consumer успешно закрыт");
            } catch (Exception e) {
                log.error("Ошибка при закрытии consumer", e);
            } finally {
                consumer = null;
            }
        }
    }

    private Consumer<Long, SpecificRecordBase> getConsumer() {
        if (consumer == null) {
            consumer = kafkaClient.getConsumerAction();
        }
        return consumer;
    }
}
