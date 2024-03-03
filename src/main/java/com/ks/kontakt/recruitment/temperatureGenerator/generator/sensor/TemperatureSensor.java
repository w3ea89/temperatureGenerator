package com.ks.kontakt.recruitment.temperatureGenerator.generator.sensor;

import com.ks.kontakt.model.TemperatureMeasurement;
import com.ks.kontakt.recruitment.temperatureGenerator.generator.configuration.TemperatureConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TemperatureSensor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureSensor.class);
    private static final AtomicInteger nextId = new AtomicInteger(0);
    private final int sensorId;
    private final int roomId;
    private final KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate;
    private final String topic;
    private final double anomalyChance;
    private final double normalRangeMin;
    private final double normalRangeMax;
    private final double anomalyRangeMin;
    private final double anomalyRangeMax;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicLong messagesSentCounter;

    public TemperatureSensor(int roomId,
                             AtomicLong messagesSent,
                             KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate,
                             String topic,
                             TemperatureConfigurationProvider temperatureConfigurationProvider) {
        this.roomId = roomId;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.messagesSentCounter = messagesSent;
        this.sensorId = nextId.incrementAndGet();
        this.anomalyChance = temperatureConfigurationProvider.getAnomalyChance();
        this.normalRangeMin = temperatureConfigurationProvider.getNormalRangeMin();
        this.normalRangeMax = temperatureConfigurationProvider.getNormalRangeMax();
        this.anomalyRangeMin = temperatureConfigurationProvider.getAnomalyRangeMin();
        this.anomalyRangeMax = temperatureConfigurationProvider.getAnomalyRangeMax();
    }

    @Override
    public void run() {
        while (running.get()) {
            double temperature = generateRandomTemperature();
            LOGGER.debug("Sensor {} in room {} generated temperature {}", sensorId, roomId, temperature);
            messagesSentCounter.incrementAndGet();
            kafkaTemplate.send(topic, "Sensor" + sensorId, buildMeasurement(temperature));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private TemperatureMeasurement buildMeasurement(double temperature) {
        return new TemperatureMeasurement(temperature, Instant.now(), roomId, sensorId);
    }

    public void stop() {
        running.set(false);
    }

    private double generateRandomTemperature() {
        if (shouldGenerateAnomaly()) {
            return ThreadLocalRandom.current().nextDouble(anomalyRangeMin, anomalyRangeMax);
        }
        return ThreadLocalRandom.current().nextDouble(normalRangeMin, normalRangeMax);

    }

    private boolean shouldGenerateAnomaly() {
        return ThreadLocalRandom.current().nextDouble(0, 1) < anomalyChance;
    }

    public int getSensorId() {
        return sensorId;
    }

    public int getRoomId() {
        return roomId;
    }

    public KafkaTemplate<String, TemperatureMeasurement> getKafkaTemplate() {
        return kafkaTemplate;
    }

    public String getTopic() {
        return topic;
    }

    public double getAnomalyChance() {
        return anomalyChance;
    }

    public double getNormalRangeMin() {
        return normalRangeMin;
    }

    public double getNormalRangeMax() {
        return normalRangeMax;
    }

    public double getAnomalyRangeMin() {
        return anomalyRangeMin;
    }

    public double getAnomalyRangeMax() {
        return anomalyRangeMax;
    }

    public AtomicBoolean getRunning() {
        return running;
    }

    public AtomicLong getMessagesSentCounter() {
        return messagesSentCounter;
    }
}
