package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CosSimilarityService {
    
    private final Map<Long, Map<Long, Double>> eventWeights = new ConcurrentHashMap<>();
    
    public void updateEventWeight(Long eventId, Long userId, Double weight) {
        eventWeights.computeIfAbsent(eventId, k -> new ConcurrentHashMap<>())
                    .merge(userId, weight, Math::max);
        log.debug("Updated event {} user {} weight: {}", eventId, userId, weight);
    }
    
    public Map<Long, Double> getEventWeights(Long eventId) {
        return eventWeights.getOrDefault(eventId, Map.of());
    }
    
    /**
     * Возвращает множество всех eventId, для которых есть хотя бы один вес пользователя.
     */
    public Set<Long> getAllEventIds() {
        return new HashSet<>(eventWeights.keySet());
    }
    
    /**
     * Вычисляет косинусное сходство между двумя мероприятиями на основе весов пользователей.
     * @param eventA идентификатор первого мероприятия
     * @param eventB идентификатор второго мероприятия
     * @return значение сходства в диапазоне [0, 1]
     */
    public double calculate(Long eventA, Long eventB) {
        Map<Long, Double> weightsA = eventWeights.getOrDefault(eventA, Map.of());
        Map<Long, Double> weightsB = eventWeights.getOrDefault(eventB, Map.of());
        
        if (weightsA.isEmpty() || weightsB.isEmpty()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (Map.Entry<Long, Double> entry : weightsA.entrySet()) {
            Long userId = entry.getKey();
            double weightA = entry.getValue();
            normA += weightA * weightA;
            
            Double weightB = weightsB.get(userId);
            if (weightB != null) {
                dotProduct += weightA * weightB;
            }
        }
        
        for (Double weightB : weightsB.values()) {
            normB += weightB * weightB;
        }
        
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
