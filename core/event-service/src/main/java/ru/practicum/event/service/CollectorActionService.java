package ru.practicum.event.service;

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
    
    public void sendView(long userId, long eventId) {
        sendAction(userId, eventId, ActionTypeProto.VIEW);
    }
    
    public void sendLike(long userId, long eventId) {
        sendAction(userId, eventId, ActionTypeProto.LIKE);
    }
    
    public void sendRegister(long userId, long eventId) {
        sendAction(userId, eventId, ActionTypeProto.REGISTER);
    }
    
    private void sendAction(long userId, long eventId, ActionTypeProto actionType) {
        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build())
                .build();
        
        collectorClient.sendUserAction(action);
        log.info("Sent {} action: user={}, event={}", actionType.name(), userId, eventId);
    }
}
