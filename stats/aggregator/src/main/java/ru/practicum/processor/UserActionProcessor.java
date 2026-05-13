package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.CosSimilarityService;
import ru.practicum.service.CosProducerService;

import java.util.Map;
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

        // Получаем старый вес пользователя для этого мероприятия
        Map<Long, Double> eventWeights = cosService.getEventWeights(eventId);
        Double oldWeight = eventWeights.get(userId);

        // Обновляем вес (после обновления пользователь будет связан с eventId)
        cosService.updateEventWeight(eventId, userId, weight);

        // Если вес не изменился (был больше или равен новому) — пересчёт не требуется
        if (oldWeight != null && oldWeight >= weight) {
            log.info("Weight for user {} event {} not changed (old={}, new={}), skipping similarity recalculation",
                    userId, eventId, oldWeight, weight);
            return;
        }

        log.info("=== PROCESSING: userId={}, eventId={}, weight={} (oldWeight={}) ===",
                userId, eventId, weight, oldWeight);

        // Получаем мероприятия, с которыми пользователь уже взаимодействовал
        Set<Long> userEvents = cosService.getUserEvents(userId);
        log.info("User events BEFORE update: {}", userEvents);

        // Добавляем текущее мероприятие в список для пересчёта (если его ещё нет)
        userEvents.add(eventId);
        log.info("User events AFTER add current: {}", userEvents);
        log.info("All events in system: {}", cosService.getAllEventIds());

        // Пересчитываем similarity ТОЛЬКО для пар с участием eventId
        // и ТОЛЬКО для мероприятий, с которыми пользователь уже взаимодействовал
        int sentCount = 0;
        for (Long otherEventId : userEvents) {
            if (otherEventId.equals(eventId)) continue;

            double similarity = cosService.calculate(eventId, otherEventId);
            long timestamp = userAction.getTimestamp().toEpochMilli();

            long eventA = Math.min(eventId, otherEventId);
            long eventB = Math.max(eventId, otherEventId);

            cosProducer.send(eventA, eventB, similarity, timestamp);
            log.info("Similarity {} <-> {} = {}", eventA, eventB, similarity);
            sentCount++;
        }
        log.info("Total similarities sent for this action: {}", sentCount);
        log.info("==================================================");
    }

    private double getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case LIKE -> 1.0;
            case REGISTER -> 0.8;
            case VIEW -> 0.4;
        };
    }
}
