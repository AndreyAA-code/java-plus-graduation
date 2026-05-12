package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.processor.SimilarityProcessor;
import ru.practicum.stats.common.config.KafkaTopicsProperties;

@SpringBootApplication(scanBasePackages = {"ru.practicum", "ru.practicum.stats.common"})
@EnableDiscoveryClient
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class AggregatorApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AggregatorApp.class, args);
        
        // Запускаем SimilarityProcessor в отдельном потоке
        SimilarityProcessor processor = context.getBean(SimilarityProcessor.class);
        Thread processorThread = new Thread(() -> {
            try {
                processor.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        processorThread.setName("similarity-processor");
        processorThread.start();
    }
}
