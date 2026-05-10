package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityService {
    
    private final EventSimilarityRepository eventSimilarityRepository;
    
    @Transactional
    public void saveEventSimilarity(EventSimilarityAvro similarityAvro) {
        Long eventA = similarityAvro.getEventA();
        Long eventB = similarityAvro.getEventB();
        Double score = similarityAvro.getScore();
        
        EventSimilarity similarity = eventSimilarityRepository
                .findByEventAAndEventB(eventA, eventB)
                .orElse(EventSimilarity.builder()
                        .eventA(eventA)
                        .eventB(eventB)
                        .build());
        
        similarity.setScore(score);
        eventSimilarityRepository.save(similarity);
        
        log.info("Saved event similarity: {} <-> {} = {}", eventA, eventB, score);
    }
}
