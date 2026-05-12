package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.CosSimilarityService;
import ru.practicum.service.CosProducerService;

import java.util.Set;

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
        
        // Получаем старые веса для этого события до обновления
        Set<Long> oldEventIds = cosService.getAllEventIds();
        
        cosService.updateEventWeight(eventId, userId, weight);
        
        // Получаем обновлённый список мероприятий
        Set<Long> allEventIds = cosService.getAllEventIds();
        
        // Пересчитываем similarity только для пар с участием eventId
        for (Long otherEventId : allEventIds) {
            if (otherEventId.equals(eventId)) continue;
            
            double similarity = cosService.calculate(eventId, otherEventId);
            long timestamp = userAction.getTimestamp().toEpochMilli();
            
            long eventA = Math.min(eventId, otherEventId);
            long eventB = Math.max(eventId, otherEventId);
            
            cosProducer.send(eventA, eventB, similarity, timestamp);
            log.info("Similarity {} <-> {} = {}", eventA, eventB, similarity);
        }
    }
    
    private double getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case LIKE -> 9.0;
            case REGISTER -> 3.0;
            case VIEW -> 1.0;
        };
    }
}
