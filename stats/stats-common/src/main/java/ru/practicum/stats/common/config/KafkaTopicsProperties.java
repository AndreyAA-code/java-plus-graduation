package ru.practicum.stats.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicsProperties {
    private String userActions = "stats.user-actions.v1";
    private String eventsSimilarity = "stats.events-similarity.v1";
}
