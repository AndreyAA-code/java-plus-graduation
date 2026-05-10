package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EventSimilarity;

import java.util.Optional;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    
    Optional<EventSimilarity> findByEventAAndEventB(Long eventA, Long eventB);
}
