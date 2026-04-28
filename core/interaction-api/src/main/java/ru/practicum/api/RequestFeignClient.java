package ru.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.requests.ParticipationRequestDto;

import java.util.List;

@FeignClient(name = "main-service", fallback = RequestFeignClientFallback.class)
public interface RequestFeignClient {
    @GetMapping("/internal/users/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> getUsersRequestsForUserEvent(@PathVariable("userId") Long userId,
                                                               @PathVariable("eventId") Long eventId);

    @PostMapping("/internal/users/{userId}/events/{eventId}/requests")
    EventRequestStatusUpdateResult updateRequestStatus(@PathVariable("userId") Long userId,
                                                       @PathVariable("eventId") Long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest request);
}