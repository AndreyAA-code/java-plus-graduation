package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.CosSimilarityService;
import ru.practicum.service.CosProducerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionProcessor {
    
    private final CosSimilarityService cosService;
    private final CosProducerService cosProducer;
    
    public void processUserAction(UserActionAvro userAction) {
        Long eventId = userAction.getEventId();
        Long userId = userAction.getUserId();
        
        double weight = getWeight(userAction.getActionType());
        
        cosService.updateEventWeight(eventId, userId, weight);
        
        // Получаем список всех мероприятий, для которых есть веса
        cosService.getAllEventIds().stream()
                .filter(otherEventId -> !otherEventId.equals(eventId))
                .forEach(otherEventId -> {
                    double similarity = cosService.calculate(eventId, otherEventId);
                    long timestamp = userAction.getTimestamp().toEpochMilli();
                    cosProducer.send(eventId, otherEventId, similarity, timestamp);
                    log.info("Similarity {} <-> {} = {}", eventId, otherEventId, similarity);
                });
    }
    
    private double getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case LIKE -> 5.0;
            case REGISTER -> 3.0;
            case VIEW -> 1.0;
        };
    }
}
