package ru.practicum.api;

import org.springframework.stereotype.Component;
import ru.practicum.dto.event.event.EventResponseDto;
import ru.practicum.util.EventState;

@Component
public class EventFeignClientFallback implements EventFeignClient {

    @Override
    public EventResponseDto getEventById(Long eventId) {
        EventResponseDto defaultEvent = new EventResponseDto();
        defaultEvent.setId(eventId);
        defaultEvent.setState(EventState.valueOf("PUBLISHED"));
        defaultEvent.setParticipantLimit(0);
        defaultEvent.setConfirmedRequests(0);
        return defaultEvent;
    }

    @Override
    public void updateConfirmedRequests(Long eventId, Long count) {
        System.out.println("Fallback: updateConfirmedRequests for event " + eventId + " with count " + count);
    }
}