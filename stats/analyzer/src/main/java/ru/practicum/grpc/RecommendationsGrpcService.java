package ru.practicum.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.*;
import ru.practicum.service.RecommendationService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsGrpcService extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Get recommendations for user: {}, maxResults: {}",
                    request.getUserId(), request.getMaxResults());

            recommendationService.getRecommendationsForUser(request)
                    .forEach(responseObserver::onNext);

            responseObserver.onCompleted();
            log.info("Recommendations sent for user: {}", request.getUserId());
        } catch (Exception e) {
            log.error("Error getting recommendations", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Get similar events for event: {}, userId: {}, maxResults: {}",
                    request.getEventId(), request.getUserId(), request.getMaxResults());

            recommendationService.getSimilarEvents(request)
                    .forEach(responseObserver::onNext);

            responseObserver.onCompleted();
            log.info("Similar events sent for event: {}", request.getEventId());
        } catch (Exception e) {
            log.error("Error getting similar events", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Get interactions count for {} events: {}",
                    request.getEventIdCount(), request.getEventIdList());

            recommendationService.getInteractionsCount(request)
                    .forEach(responseObserver::onNext);

            responseObserver.onCompleted();
            log.info("Interactions count sent for {} events", request.getEventIdCount());
        } catch (Exception e) {
            log.error("Error getting interactions count", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}