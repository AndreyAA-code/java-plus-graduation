package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

    @KafkaListener(
            topics = "stats.user-actions.v1",
            groupId = "aggregator-group",
            properties = {
                    "key.deserializer=org.apache.kafka.common.serialization.LongDeserializer",
                    "value.deserializer=ru.practicum.deserializer.UserActionAvroDeserializer"
            }
    )
    public void process(ConsumerRecord<Long, UserActionAvro> record) {
        Long key = record.key();
        UserActionAvro action = record.value();

        log.info("Получено действие: key={}, userId={}, eventId={}, type={}",
                key, action.getUserId(), action.getEventId(), action.getActionType());

        List<EventSimilarityAvro> similarities = calculator.calculateSimilarity(action);

        for (EventSimilarityAvro similarity : similarities) {
            producer.send(similarity);  // ✅ один аргумент
            log.info("Отправлено сходство: {} <-> {} = {}",
                    similarity.getEventA(), similarity.getEventB(), similarity.getScore());
        }
    }
}