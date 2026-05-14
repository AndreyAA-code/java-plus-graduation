package ru.practicum.controller;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.producer.UserActionProducer;

import java.time.Instant;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserActionGrpcController extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final UserActionProducer userActionProducer;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received user action: userId={}, eventId={}, actionType={}",
                    request.getUserId(), request.getEventId(), request.getActionType());

            UserActionAvro userAction = UserActionAvro.newBuilder()
                    .setUserId(request.getUserId())
                    .setEventId(request.getEventId())
                    .setActionType(convertActionType(request.getActionType()))
                    .setTimestamp(convertTimestamp(request.getTimestamp()))
                    .build();

            userActionProducer.sendUserAction(userAction);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            log.debug("User action processed successfully");
        } catch (Exception e) {
            log.error("Error processing user action", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private ActionTypeAvro convertActionType(ru.practicum.ewm.stats.proto.ActionTypeProto actionType) {
        return ActionTypeAvro.valueOf(actionType.name());
    }

    private Instant convertTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return Instant.now();
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
