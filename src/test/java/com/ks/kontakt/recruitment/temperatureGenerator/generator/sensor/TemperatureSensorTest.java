package com.ks.kontakt.recruitment.temperatureGenerator.generator.sensor;


import com.ks.kontakt.model.TemperatureMeasurement;
import com.ks.kontakt.recruitment.temperatureGenerator.generator.configuration.TemperatureConfigurationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TemperatureSensorTest {
    private final static String TOPIC = "Topic";
    private final AtomicLong messagesSentCounter = new AtomicLong(0);
    private KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate = mock(KafkaTemplate.class);
    private TemperatureConfigurationProvider temperatureConfigurationProvider = mock(TemperatureConfigurationProvider.class);

    @BeforeEach
    void setUp() {
        when(temperatureConfigurationProvider.getAnomalyChance()).thenReturn(15.0);
        when(temperatureConfigurationProvider.getNormalRangeMin()).thenReturn(15.0);
        when(temperatureConfigurationProvider.getNormalRangeMax()).thenReturn(25.0);
        when(temperatureConfigurationProvider.getAnomalyRangeMin()).thenReturn(29.0);
        when(temperatureConfigurationProvider.getAnomalyRangeMax()).thenReturn(35.0);
    }

    @Test
    void testRunSendsTemperatureMeasurementWhenAnomalyIsDisabled() {
        when(temperatureConfigurationProvider.getAnomalyChance()).thenReturn(0.0);
        TemperatureSensor temperatureSensor = new TemperatureSensor(1, messagesSentCounter,
                kafkaTemplate, TOPIC, temperatureConfigurationProvider);
        ArgumentCaptor<TemperatureMeasurement> measurementCaptor = ArgumentCaptor.forClass(TemperatureMeasurement.class);
        scheduleEnd(temperatureSensor);
        temperatureSensor.run();
        verify(kafkaTemplate, atLeastOnce()).send(eq(TOPIC), argThat(s -> s.startsWith("Sensor")), measurementCaptor.capture());
        TemperatureMeasurement capturedMeasurement = measurementCaptor.getAllValues().get(0);
        assertThat(capturedMeasurement.getThermometerId()).isEqualTo(temperatureSensor.getSensorId());
        assertThat(capturedMeasurement.getRoomId()).isEqualTo(1);
        assertThat(capturedMeasurement.getTemperature()).isGreaterThanOrEqualTo(15).isLessThanOrEqualTo(25.0);
        assertThat(messagesSentCounter.get()).isGreaterThanOrEqualTo(1l);
    }

    @Test
    void testRunSendsTemperatureMeasurementWhenAnomalyIsGenerated() {
        when(temperatureConfigurationProvider.getAnomalyChance()).thenReturn(1.0);
        TemperatureSensor temperatureSensor = new TemperatureSensor(1, messagesSentCounter,
                kafkaTemplate, TOPIC, temperatureConfigurationProvider);
        ArgumentCaptor<TemperatureMeasurement> measurementCaptor = ArgumentCaptor.forClass(TemperatureMeasurement.class);
        scheduleEnd(temperatureSensor);
        temperatureSensor.run();
        verify(kafkaTemplate, atLeastOnce()).send(eq(TOPIC), argThat(s -> s.startsWith("Sensor")), measurementCaptor.capture());
        TemperatureMeasurement capturedMeasurement = measurementCaptor.getAllValues().get(0);
        assertThat(capturedMeasurement.getThermometerId()).isEqualTo(temperatureSensor.getSensorId());
        assertThat(capturedMeasurement.getRoomId()).isEqualTo(1);
        assertThat(capturedMeasurement.getTemperature()).isGreaterThanOrEqualTo(29).isLessThanOrEqualTo(35.0);
        assertThat(messagesSentCounter.get()).isGreaterThanOrEqualTo(1l);
    }

    private void scheduleEnd(TemperatureSensor sensor) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(sensor::stop, 3, TimeUnit.SECONDS);
    }
}
