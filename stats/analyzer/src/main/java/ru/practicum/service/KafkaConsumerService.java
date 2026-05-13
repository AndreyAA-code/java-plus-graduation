package ru.practicum.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;
import ru.practicum.consumer.EventSimilarityConsumer;
import ru.practicum.consumer.UserActionConsumer;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UserActionConsumer userActionConsumer;
    private final EventSimilarityConsumer eventSimilarityConsumer;
    private final RecommendationService recommendationService;

    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        userActionConsumer.subscribe();
        eventSimilarityConsumer.subscribe();

        Thread consumerThread = new Thread(this::consumeLoop, "kafka-analyzer-consumer");
        consumerThread.setDaemon(true);
        consumerThread.start();
        log.info("Kafka consumer service started");
    }

    private void consumeLoop() {
        while (running) {
            try {
                ConsumerRecords<Long, SpecificRecordBase> actionRecords = 
                    userActionConsumer.poll(Duration.ofMillis(1000));
                
                for (var record : actionRecords) {
                    UserActionAvro action = (UserActionAvro) record.value();
                    log.info("Received user action: userId={}, eventId={}, type={}",
                        action.getUserId(), action.getEventId(), action.getActionType());

                    recommendationService.saveUserAction(action);

                    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                    offsets.put(new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1));
                    userActionConsumer.commitSync(offsets);
                }

                ConsumerRecords<Long, SpecificRecordBase> similarityRecords = 
                    eventSimilarityConsumer.poll(Duration.ofMillis(1000));
                
                for (var record : similarityRecords) {
                    EventSimilarityAvro similarity = (EventSimilarityAvro) record.value();
                    log.info("Received similarity: eventA={}, eventB={}, score={}",
                        similarity.getEventA(), similarity.getEventB(), similarity.getScore());

                    recommendationService.saveEventSimilarity(similarity);

                    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                    offsets.put(new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1));
                    eventSimilarityConsumer.commitSync(offsets);
                }

            } catch (Exception e) {
                log.error("Error consuming messages", e);
            }
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        userActionConsumer.wakeup();
        eventSimilarityConsumer.wakeup();
        userActionConsumer.close();
        eventSimilarityConsumer.close();
        log.info("Kafka consumer service stopped");
    }
}