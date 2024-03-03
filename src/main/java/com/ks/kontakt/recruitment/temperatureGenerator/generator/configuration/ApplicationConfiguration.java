package com.ks.kontakt.recruitment.temperatureGenerator.generator.configuration;

import com.ks.kontakt.model.TemperatureMeasurement;
import com.ks.kontakt.recruitment.temperatureGenerator.generator.sensor.TemperatureReaderSimulator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.Executors;

@Configuration
public class ApplicationConfiguration {
    @Value("${temperatureConfiguration.anomalyChance}")
    private double anomalyChance;
    @Value("${temperatureConfiguration.normalRangeMin}")
    private double normalRangeMin;
    @Value("${temperatureConfiguration.normalRangeMax}")
    private double normalRangeMax;
    @Value("${temperatureConfiguration.anomalyRangeMin}")
    private double anomalyRangeMin;
    @Value("${temperatureConfiguration.anomalyRangeMax}")
    private double anomalyRangeMax;
    @Value("${topic}")
    private String topicName;
    @Value("${sensorNumber}")
    private int sensorNumbers;

    @Bean
    public TemperatureConfigurationProvider temperatureConfigurationProvider() {
        return new TemperatureConfigurationProvider(anomalyChance,
                normalRangeMin,
                normalRangeMax,
                anomalyRangeMin,
                anomalyRangeMax);
    }

    @Bean
    public TemperatureReaderSimulator temperatureReaderSimulator(KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate) {
        return new TemperatureReaderSimulator(
                Executors.newScheduledThreadPool(1),
                Executors.newFixedThreadPool(sensorNumbers),
                temperatureConfigurationProvider(),
                kafkaTemplate,
                topicName,
                sensorNumbers);


    }
}
