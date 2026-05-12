package ru.practicum.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.common.config.KafkaClient;
import ru.practicum.service.EventSimilarityService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityConsumer {
    
    private final KafkaClient kafkaClient;
    private final EventSimilarityService eventSimilarityService;
    private volatile boolean running = true;
    
    @PostConstruct
    public void start() {
        Thread consumerThread = new Thread(this::consume);
        consumerThread.setName("event-similarity-consumer-thread");
        consumerThread.start();
        log.info("Event similarity consumer thread started");
    }
    
    private void consume() {
        var consumer = kafkaClient.getConsumerSimilarity();
        consumer.subscribe(java.util.List.of("stats.events-similarity.v1"));
        log.info("Subscribed to topic: stats.events-similarity.v1");
        
        while (running) {
            try {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    EventSimilarityAvro similarity = (EventSimilarityAvro) record.value();
                    log.info("Received event similarity: eventA={}, eventB={}, score={}",
                            similarity.getEventA(), similarity.getEventB(), similarity.getScore());
                    eventSimilarityService.saveEventSimilarity(similarity);
                }
                if (!records.isEmpty()) {
                    consumer.commitSync();
                    log.debug("Committed offsets for {} records", records.count());
                }
            } catch (Exception e) {
                log.error("Error consuming event similarity", e);
            }
        }
    }
    
    @PreDestroy
    public void stop() {
        running = false;
        log.info("Event similarity consumer stopped");
    }
}
