package ru.practicum.request.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.CollectorClient;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionProto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorActionService {
    
    private final CollectorClient collectorClient;
    
    public void sendRegister(long userId, long eventId) {
        try {
            UserActionProto action = UserActionProto.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(ActionTypeProto.REGISTER)
                    .setTimestamp(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build())
                    .build();
            
            collectorClient.sendUserAction(action);
            log.info("Sent REGISTER action: user={}, event={}", userId, eventId);
        } catch (Exception e) {
            log.error("Failed to send REGISTER action: {}", e.getMessage());
        }
    }
}
