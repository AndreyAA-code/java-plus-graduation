package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.AnalyzerClient;
import ru.practicum.dto.event.event.EventResponseDto;
import ru.practicum.dto.event.event.EventSearchCriteria;
import ru.practicum.dto.event.event.ShortEventResponseDto;
import ru.practicum.event.service.CollectorActionService;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;
import ru.practicum.service.event.EventService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventsController {

    private final EventService service;
    private final CollectorActionService collectorActionService;
    private final AnalyzerClient analyzerClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShortEventResponseDto> findAll(@ModelAttribute EventSearchCriteria criteria, HttpServletRequest req) throws Exception {
       log.info("Find all events");
       List<ShortEventResponseDto> res = service.find(criteria);
       return res;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto getEvent(@PathVariable Long id, HttpServletRequest req) {
        log.info("Get event by id {}", id);
        EventResponseDto res = service.get(id, req);
        
        String userIdHeader = req.getHeader("X-EWM-USER-ID");
        if (userIdHeader != null) {
            try {
                long userId = Long.parseLong(userIdHeader);
                collectorActionService.sendView(userId, id);
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID header: {}", userIdHeader);
            }
        }
        return res;
    }

    @GetMapping("/location/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ShortEventResponseDto> findEventsByLocation(@PathVariable Long locationId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      HttpServletRequest req) {

        log.info("Find events by location {}", locationId);
        return service.findEventsByLocation(locationId, PageRequest.of(from / size, size, Sort.by("event_date").descending()));
    }

    @GetMapping("/near")
    @ResponseStatus(HttpStatus.OK)
    public List<ShortEventResponseDto> findEventsNear(@RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
                                                      @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") Double lon,
                                                      @RequestParam(defaultValue = "1.0") @DecimalMin("0.1") Double radius,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {

        log.info("Find events in locations where user is located: lat={}, lon={}, from={}, size={}",
                lat, lon, from, size);

        return service.findEventsNear(lat, lon, radius,
                PageRequest.of(from / size, size, Sort.by("event_date").descending()));
    }

    @PutMapping("/{eventId}/like")
    public ResponseEntity<Void> likeEvent(@PathVariable long eventId,
                                          @RequestHeader("X-EWM-USER-ID") long userId) {
        log.info("Like event {} from user {}", eventId, userId);
        collectorActionService.sendLike(userId, eventId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recommendations")
    public Stream<ShortEventResponseDto> getRecommendations(
            @RequestHeader("X-EWM-USER-ID") Long userId,
            @RequestParam(defaultValue = "10") int maxResults) {
        log.info("Get recommendations for user {}", userId);

        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        Stream<RecommendedEventProto> recommendations = analyzerClient.getRecommendationsForUser(request);
        
        List<Long> recommendedEventIds = recommendations
                .map(RecommendedEventProto::getEventId)
                .collect(Collectors.toList());

        if (recommendedEventIds.isEmpty()) {
            return Stream.empty();
        }

        List<ShortEventResponseDto> events = service.findAllById(recommendedEventIds);
        return events.stream();
    }
}
