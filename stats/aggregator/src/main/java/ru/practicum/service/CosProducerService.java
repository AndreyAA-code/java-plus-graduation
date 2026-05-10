package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CosProducerService {
    
    private final KafkaTemplate<Long, EventSimilarityAvro> kafkaTemplate;
    
    @Value("${kafka.topics.stats-events-similarity-v1:stats.events-similarity.v1}")
    private String similarityTopic;
    
    public void send(Long eventA, Long eventB, double score, long timestamp) {
        long orderedA = Math.min(eventA, eventB);
        long orderedB = Math.max(eventA, eventB);
        
        EventSimilarityAvro similarity = EventSimilarityAvro.newBuilder()
                .setEventA(orderedA)
                .setEventB(orderedB)
                .setScore(score)
                .setTimestamp(Instant.ofEpochSecond(timestamp))
                .build();
        
        kafkaTemplate.send(similarityTopic, orderedA, similarity)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent similarity: {} <-> {} = {}", orderedA, orderedB, score);
                    } else {
                        log.error("Failed to send similarity: {}", ex.getMessage());
                    }
                });
    }
}
