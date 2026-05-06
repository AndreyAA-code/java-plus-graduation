package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.producer.UserActionProducer;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserActionProcessor {

    private final UserActionMapper userActionMapper;
    private final UserActionProducer userActionProducer;

    public void processUserAction(UserActionProto request) {
        log.info("Processing: userId={}, eventId={}",
                request.getUserId(), request.getEventId());

        var userActionAvro = userActionMapper.mapToAvro(request);

        userActionProducer.sendUserAction(userActionAvro);

        log.info("Sent to Kafka: userId={}, eventId={}",
                request.getUserId(), request.getEventId());
    }
}
