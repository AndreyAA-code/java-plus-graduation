package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;
import ru.practicum.stats.common.config.KafkaTopicsProperties;

@SpringBootApplication(scanBasePackages = {"ru.practicum", "ru.practicum.stats.common"})
@EnableKafka
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class AnalyzerApp {
    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApp.class, args);
    }
}
