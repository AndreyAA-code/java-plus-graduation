package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.producer.EventSimilarityProducer;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarityProcessor {
    private final SimilarityCalculator calculator;
    private final EventSimilarityProducer producer;

    @KafkaListener(topics = "stats.user-actions.v1", groupId = "aggregator-group")
    public void process(UserActionAvro action) {
        log.info("Получено действие: userId={}, eventId={}, type={}", 
                action.getUserId(), action.getEventId(), action.getActionType());
        
        List<EventSimilarityAvro> similarities = calculator.calculateSimilarity(action);
        
        for (EventSimilarityAvro similarity : similarities) {
            producer.send(similarity);
            log.info("Отправлено сходство: {} <-> {} = {}", 
                    similarity.getEventA(), similarity.getEventB(), similarity.getScore());
        }
    }
}
