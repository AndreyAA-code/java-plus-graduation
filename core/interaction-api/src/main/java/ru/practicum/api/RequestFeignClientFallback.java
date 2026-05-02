package ru.practicum.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.event.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class RequestFeignClientFallback implements RequestFeignClient {

    @Override
    public List<ParticipationRequestDto> getUsersRequestsForUserEvent(Long userId, Long eventId) {
        log.info("Fallback: getUsersRequestsForUserEvent for eventId = " + eventId);
        return Collections.emptyList();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(Collections.emptyList());
        result.setRejectedRequests(Collections.emptyList());
        log.info("Fallback: updateRequestStatus for eventId = " + eventId);
        return result;
    }
}