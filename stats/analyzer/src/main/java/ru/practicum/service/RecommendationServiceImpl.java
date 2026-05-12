package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;
import ru.practicum.mapper.RecommendationsMapper;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;
    private final RecommendationsMapper recommendationsMapper;

    @Override
    public Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        log.info("Getting recommendations for user: {}, maxResults: {}", userId, maxResults);

        Set<Long> userEvents = userActionRepository.findEventIdsByUserId(userId);
        
        if (userEvents.isEmpty()) {
            log.info("User {} has no interactions", userId);
            return Stream.empty();
        }
        
        List<Long> recentEvents = userActionRepository.findRecentEventIdsByUserId(
            userId, 
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "timestampAction"))
        );
        
        Map<Long, Double> recommendations = new HashMap<>();
        
        for (Long recentEvent : recentEvents) {
            List<EventSimilarity> similarities = eventSimilarityRepository.findAllByEventId(recentEvent);
            
            for (EventSimilarity sim : similarities) {
                long candidateId = sim.getEventA().equals(recentEvent) ? sim.getEventB() : sim.getEventA();
                
                if (!userEvents.contains(candidateId)) {
                    recommendations.merge(candidateId, sim.getScore(), Math::max);
                }
            }
        }
        
        return recommendations.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build());
    }

    @Override
    public Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        log.info("Getting similar events for event: {}, user: {}, maxResults: {}", eventId, userId, maxResults);

        List<EventSimilarity> allSimilarities = eventSimilarityRepository.findAllByEventId(eventId);
        Set<Long> userEvents = userActionRepository.findEventIdsByUserId(userId);

        List<EventSimilarity> filtered = allSimilarities.stream()
                .filter(sim -> {
                    long otherId = sim.getEventA().equals(eventId) ? sim.getEventB() : sim.getEventA();
                    return !userEvents.contains(otherId);
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(maxResults)
                .collect(Collectors.toList());

        return recommendationsMapper.mapToProto(filtered, eventId);
    }

    @Override
    public Stream<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        List<Long> eventIds = request.getEventIdList();
        Map<Long, Double> result = new HashMap<>();

        List<UserAction> userActions = userActionRepository.findByEventIdIn(new HashSet<>(eventIds));

        for (Long eventId : eventIds) {
            Map<Long, Double> userMaxScores = userActions.stream()
                    .filter(action -> action.getEventId().equals(eventId))
                    .collect(Collectors.toMap(
                            UserAction::getUserId,
                            UserAction::getUserScore,
                            Math::max
                    ));

            double sum = userMaxScores.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

            result.put(eventId, sum);
            log.info("Event {}: sum = {}", eventId, sum);
        }

        return recommendationsMapper.mapToProto(result);
    }


}
