package com.ks.kontakt.recruitment.temperatureGenerator.generator.sensor;

import com.ks.kontakt.model.TemperatureMeasurement;
import com.ks.kontakt.recruitment.temperatureGenerator.generator.configuration.TemperatureConfigurationProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TemperatureReaderSimulatorTest {
    private static final String TOPIC = "TOPICNAME";
    private static final int THREADS_NUMBER = 10;
    private final KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate = mock(KafkaTemplate.class);
    private final TemperatureConfigurationProvider temperatureConfigurationProvider = mock(TemperatureConfigurationProvider.class);
    private final ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
    private final ExecutorService executorService = mock(ExecutorService.class);
    private final TemperatureReaderSimulator temperatureReaderSimulator = new TemperatureReaderSimulator(scheduledExecutorService, executorService, temperatureConfigurationProvider, kafkaTemplate, TOPIC, THREADS_NUMBER);

    @Test
    public void testSimulateSensorsSendsMessages() {
        temperatureReaderSimulator.simulateSensors();
        verify(executorService, times(THREADS_NUMBER)).submit(any(TemperatureSensor.class));
    }

    @Test
    public void verifyIfKafkaIsSetProperly() {
        temperatureReaderSimulator.simulateSensors();
        ArgumentCaptor<TemperatureSensor> temperatureSensorArgumentCaptor = ArgumentCaptor.forClass(TemperatureSensor.class);
        verify(executorService, times(THREADS_NUMBER)).submit(temperatureSensorArgumentCaptor.capture());
        assertThat(temperatureSensorArgumentCaptor.getAllValues().get(0).getKafkaTemplate()).isEqualTo(kafkaTemplate);
    }

    @Test
    public void verifyIfMinNormalTemperatureIsSetProperly() {
        //given
        double expectedTemp = 20.0;
        when(temperatureConfigurationProvider.getNormalRangeMin()).thenReturn(expectedTemp);
        ArgumentCaptor<TemperatureSensor> temperatureSensorArgumentCaptor = ArgumentCaptor.forClass(TemperatureSensor.class);
        //when
        temperatureReaderSimulator.simulateSensors();
        //then
        verify(executorService, times(THREADS_NUMBER)).submit(temperatureSensorArgumentCaptor.capture());
        assertThat(temperatureSensorArgumentCaptor.getAllValues().get(0).getNormalRangeMin()).isEqualTo(expectedTemp);
    }

    /// and more similar test for each ......
}
