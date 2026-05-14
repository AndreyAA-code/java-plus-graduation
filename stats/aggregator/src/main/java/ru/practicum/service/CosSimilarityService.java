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

    public Set<Long> getAllEventIds() {
        return new HashSet<>(eventWeights.keySet());
    }

    public double calculate(Long eventA, Long eventB) {
        Map<Long, Double> weightsA = eventWeights.getOrDefault(eventA, Map.of());
        Map<Long, Double> weightsB = eventWeights.getOrDefault(eventB, Map.of());

        if (weightsA.isEmpty() || weightsB.isEmpty()) return 0.0;

        double sMin = 0.0, sA = 0.0, sB = 0.0;
        Set<Long> allUsers = new HashSet<>();
        allUsers.addAll(weightsA.keySet());
        allUsers.addAll(weightsB.keySet());

        for (Long userId : allUsers) {
            double wA = weightsA.getOrDefault(userId, 0.0);
            double wB = weightsB.getOrDefault(userId, 0.0);
            sMin += Math.min(wA, wB);
            sA += wA;
            sB += wB;
        }

        if (sA == 0.0 || sB == 0.0) return 0.0;
        return sMin / (Math.sqrt(sA) * Math.sqrt(sB));
    }

    public Set<Long> getUserEvents(Long userId) {
        Set<Long> events = new HashSet<>();
        for (Map.Entry<Long, Map<Long, Double>> entry : eventWeights.entrySet()) {
            if (entry.getValue().containsKey(userId)) events.add(entry.getKey());
        }
        return events;
    }
}
