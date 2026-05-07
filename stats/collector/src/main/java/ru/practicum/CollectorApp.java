package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.practicum.stats.common.config.KafkaTopicsProperties;

@SpringBootApplication(scanBasePackages = {"ru.practicum", "ru.practicum.stats.common"})
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class CollectorApp {
    public static void main(String[] args) {
        SpringApplication.run(CollectorApp.class, args);
    }
}
