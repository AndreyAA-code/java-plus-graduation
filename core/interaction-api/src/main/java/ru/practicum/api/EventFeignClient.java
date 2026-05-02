package ru.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.event.EventResponseDto;
import ru.practicum.config.FeignDateConfig;

@FeignClient(name = "event-service", configuration = FeignDateConfig.class, fallback = EventFeignClientFallback.class)
public interface EventFeignClient {

    @GetMapping("/internal/events/{eventId}")
    EventResponseDto getEventById(@PathVariable("eventId") Long eventId);

    @PostMapping("/internal/events/{eventId}/confirmed")
    void updateConfirmedRequests(@PathVariable("eventId") Long eventId,
                                 @RequestParam Long count);

}