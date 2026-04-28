package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.event.EventResponseDto;
import ru.practicum.dto.event.event.NewEventRequestDto;
import ru.practicum.dto.event.event.ShortEventResponseDto;
import ru.practicum.dto.event.event.UpdateEventRequestDto;
import ru.practicum.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<ShortEventResponseDto> getUserEvents(@PathVariable Long userId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get events for user {}", userId);
        return eventService.getAll(userId, PageRequest.of(from / size, size, Sort.by("eventDate").descending()));
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto createEvent(@PathVariable Long userId,
                                        @Valid @RequestBody NewEventRequestDto dto) {
        log.info("Create event for user {}", userId);
        return eventService.create(userId, dto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto getEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        log.info("Get event {} for user {}", eventId, userId);
        return eventService.get(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto updateEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventRequestDto dto) {
        log.info("Update event {} for user {}", eventId, userId);
        return eventService.update(userId, eventId, dto);
    }
}