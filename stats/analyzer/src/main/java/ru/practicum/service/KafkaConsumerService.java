package ru.practicum.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.consumer.UserActionConsumer;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UserActionConsumer consumer;
    private final UserActionRepository userActionRepository;
    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        consumer.subscribe();
        Thread consumerThread = new Thread(this::consume);
        consumerThread.setName("kafka-consumer-thread");
        consumerThread.start();
        log.info("Kafka consumer thread started");
    }

    @Transactional
    public void saveUserAction(UserActionAvro userAction) {
        try {
            double score = getWeight(userAction.getActionType());
            
            UserAction entity = UserAction.builder()
                    .userId(userAction.getUserId())
                    .eventId(userAction.getEventId())
                    .userScore(score)
                    .timestampAction(userAction.getTimestamp())
                    .build();
            
            userActionRepository.save(entity);
            log.info("Saved user action: userId={}, eventId={}, score={}", 
                    userAction.getUserId(), userAction.getEventId(), score);
        } catch (Exception e) {
            log.error("Error saving user action: {}", e.getMessage(), e);
        }
    }

    private void consume() {
        while (running) {
            try {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    UserActionAvro userAction = (UserActionAvro) record.value();
                    log.info("Received user action: userId={}, eventId={}, action={}", 
                            userAction.getUserId(), 
                            userAction.getEventId(), 
                            userAction.getActionType());
                    
                    saveUserAction(userAction);
                }
            } catch (Exception e) {
                log.error("Error consuming messages: {}", e.getMessage(), e);
            }
        }
    }

    private double getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case LIKE -> 5.0;
            case REGISTER -> 3.0;
            case VIEW -> 1.0;
        };
    }

    @PreDestroy
    public void stop() {
        running = false;
        consumer.wakeup();
        consumer.close();
        log.info("Kafka consumer stopped");
    }
}
