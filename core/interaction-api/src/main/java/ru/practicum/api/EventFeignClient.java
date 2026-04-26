package ru.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.event.event.EventResponseDto;

@FeignClient(name = "event-service", fallback = EventFeignClientFallback.class)
public interface EventFeignClient {

    @GetMapping("/internal/events/{eventId}")
    EventResponseDto getEventById(@PathVariable("eventId") Long eventId);

    @PatchMapping("/internal/events/{eventId}/confirmed")
    void updateConfirmedRequests(@PathVariable("eventId") Long eventId,
                                 @RequestParam Long count);

}