package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionService {

    private final UserActionRepository userActionRepository;

    @Transactional
    public void saveUserAction(UserActionAvro userActionAvro) {
        Long userId = userActionAvro.getUserId();
        Long eventId = userActionAvro.getEventId();
        double userScore = getWeight(userActionAvro.getActionType());
        Instant timestamp = userActionAvro.getTimestamp();

        UserAction existingAction = userActionRepository
                .findByUserIdAndEventId(userId, eventId)
                .orElse(null);

        if (existingAction != null) {
            // Обновляем существующую запись, если новый вес больше
            if (userScore > existingAction.getUserScore()) {
                existingAction.setUserScore(userScore);
                existingAction.setTimestampAction(timestamp);
                userActionRepository.save(existingAction);
                log.info("Updated user action: userId={}, eventId={}, score={}", userId, eventId, userScore);
            } else {
                log.debug("Existing score {} >= new score {}, skipping", existingAction.getUserScore(), userScore);
            }
        } else {
            // Создаем новую запись
            UserAction userAction = UserAction.builder()
                    .userId(userId)
                    .eventId(eventId)
                    .userScore(userScore)
                    .timestampAction(timestamp)
                    .build();
            userActionRepository.save(userAction);
            log.info("Saved user action: userId={}, eventId={}, score={}", userId, eventId, userScore);
        }
    }

    private double getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case LIKE -> 1.0;
            case REGISTER -> 0.8;
            case VIEW -> 0.4;
        };
    }
}