package ru.practicum.service.request;

import ru.practicum.dto.event.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.requests.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getOtherUsersEventsRequests(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequestStatus);

    List<ParticipationRequestDto> getUsersRequestsForUserEvent(Long userId, Long eventId);
}