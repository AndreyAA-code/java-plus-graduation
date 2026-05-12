package ru.practicum.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;
import ru.practicum.consumer.UserActionConsumer;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UserActionConsumer consumer;
    private final UserActionService userActionService;
    private volatile boolean running = true;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @PostConstruct
    public void start() {
        consumer.subscribe();
        Thread consumerThread = new Thread(this::consume);
        consumerThread.setName("analyzer-consumer-thread");
        consumerThread.start();
        log.info("Kafka consumer thread started");
    }

    private void consume() {
        while (running) {
            try {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                log.info("Polled {} records from Kafka", records.count());
                
                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    UserActionAvro userAction = (UserActionAvro) record.value();
                    log.info("Received user action: userId={}, eventId={}, action={}",
                            userAction.getUserId(), userAction.getEventId(), userAction.getActionType());
                    userActionService.saveUserAction(userAction);
                    
                    // Сохраняем offset после успешной обработки
                    currentOffsets.put(
                        new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1)
                    );
                }
                
                // Коммитим оффсеты после обработки пачки
                if (!currentOffsets.isEmpty()) {
                    consumer.commitSync(currentOffsets);
                    log.debug("Committed offsets: {}", currentOffsets);
                }
            } catch (Exception e) {
                log.error("Error consuming messages", e);
            }
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        // Финальный коммит перед остановкой
        if (!currentOffsets.isEmpty()) {
            try {
                consumer.commitSync(currentOffsets);
                log.info("Final offsets committed: {}", currentOffsets);
            } catch (Exception e) {
                log.error("Error committing final offsets", e);
            }
        }
        consumer.wakeup();
        consumer.close();
        log.info("Kafka consumer stopped");
    }
}
