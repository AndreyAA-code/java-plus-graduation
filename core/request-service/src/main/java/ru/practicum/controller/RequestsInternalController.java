package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.requests.ParticipationRequestDto;
import ru.practicum.service.ParticipationRequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/internal/users/{userId}/events/{eventId}/requests")
public class RequestsInternalController {

    private final ParticipationRequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsForEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("INTERNAL: getting requests for user {} event {}", userId, eventId);
        return requestService.getUsersRequestsForUserEvent(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("INTERNAL: updating requests status for user {} event {}", userId, eventId);
        return requestService.updateRequestStatus(userId, eventId, request);
    }
}