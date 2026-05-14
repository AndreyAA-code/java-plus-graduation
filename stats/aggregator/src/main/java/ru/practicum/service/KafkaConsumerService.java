package ru.practicum.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;
import ru.practicum.consumer.UserActionConsumer;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.processor.UserActionProcessor;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UserActionConsumer consumer;
    private final UserActionProcessor processor;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AtomicBoolean running = new AtomicBoolean(true);

    @PostConstruct
    public void start() {
        consumer.subscribe();
        executorService.submit(this::consume);
        log.info("Kafka consumer service started with ExecutorService");
    }

    private void consume() {
        while (running.get()) {
            try {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    UserActionAvro userAction = (UserActionAvro) record.value();
                    processor.processUserAction(userAction);
                }
            } catch (Exception e) {
                if (running.get()) {
                    log.error("Error consuming messages", e);
                }
            }
        }
    }

    @PreDestroy
    public void stop() {
        running.set(false);
        consumer.wakeup();

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("ExecutorService did not terminate in time, forcing shutdown");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for executor termination", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        consumer.close();
        log.info("Kafka consumer stopped");
    }
}
