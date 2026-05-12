package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import ru.practicum.stats.common.config.KafkaTopicsProperties;

@EnableKafka
@SpringBootApplication(scanBasePackages = {"ru.practicum", "ru.practicum.stats.common"})
@EnableDiscoveryClient
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class AggregatorApp {
    public static void main(String[] args) {
        SpringApplication.run(AggregatorApp.class, args);
    }
}
