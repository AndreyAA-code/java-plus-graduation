package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.ParticipationRequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class EventRequestController {

    private final ParticipationRequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("Get requests for event {} by user {}", eventId, userId);
        return requestService.getUsersRequestsForUserEvent(userId, eventId);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Update request status for event {} by user {} (from EventRequestController)", eventId, userId);
        return requestService.updateRequestStatus(userId, eventId, request);
    }
}
