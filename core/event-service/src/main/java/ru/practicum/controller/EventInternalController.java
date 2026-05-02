package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.event.EventResponseDto;
import ru.practicum.service.event.EventService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/events")
public class EventInternalController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto getEventById(@PathVariable Long eventId) {
        log.info("INTERNAL: get event by id {}", eventId);
        return eventService.getEventById(eventId);
    }

    @PostMapping("/{eventId}/confirmed")
    @ResponseStatus(HttpStatus.OK)
    public void updateConfirmedRequests(@PathVariable Long eventId,
                                        @RequestParam Long count) {
        log.info("INTERNAL: update confirmed requests for event {} to {}", eventId, count);
        eventService.updateConfirmedRequests(eventId, count);
    }
}