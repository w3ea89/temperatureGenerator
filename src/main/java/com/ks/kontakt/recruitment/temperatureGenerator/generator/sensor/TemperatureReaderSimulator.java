package com.ks.kontakt.recruitment.temperatureGenerator.generator.sensor;

import com.ks.kontakt.model.TemperatureMeasurement;
import com.ks.kontakt.recruitment.temperatureGenerator.generator.configuration.TemperatureConfigurationProvider;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TemperatureReaderSimulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureReaderSimulator.class);
    private final TemperatureConfigurationProvider temperatureConfigurationProvider;
    private final KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate;
    private final ScheduledExecutorService scheduler;
    private final String topicName;
    private final ExecutorService executorService;
    private final AtomicLong messagesSent = new AtomicLong(0);
    private final int threadsNumber;

    public TemperatureReaderSimulator(
            ScheduledExecutorService scheduledExecutorService,
            ExecutorService executorService,
            TemperatureConfigurationProvider temperatureConfigurationProvider,
            KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate,
            String topicName,
            int threadsNumber) {
        this.scheduler = scheduledExecutorService;
        this.executorService = executorService;
        this.temperatureConfigurationProvider = temperatureConfigurationProvider;
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
        this.threadsNumber = threadsNumber;
    }

    @PostConstruct
    public void simulateSensors() {
        scheduler.scheduleAtFixedRate(() -> {
            LOGGER.info("Messages per second: {}", messagesSent.getAndSet(0));
        }, 1, 1, TimeUnit.SECONDS);
        for (int i = 0; i < threadsNumber; i++) {
            executorService.submit(
                    new TemperatureSensor(i, messagesSent, kafkaTemplate, topicName, temperatureConfigurationProvider)
            );
        }
        executorService.shutdown();
    }

    public void stop() {
        executorService.shutdownNow();
    }
}
