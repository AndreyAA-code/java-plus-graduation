package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorActionService {
    
    public void sendRegister(long userId, long eventId) {
        try {
            log.info("Sending REGISTER action: user={}, event={}", userId, eventId);
            // TODO: заменить на gRPC вызов
        } catch (Exception e) {
            log.error("Failed to send REGISTER action: {}", e.getMessage());
        }
    }
}
