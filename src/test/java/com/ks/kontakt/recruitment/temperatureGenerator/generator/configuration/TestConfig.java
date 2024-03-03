package com.ks.kontakt.recruitment.temperatureGenerator.generator.configuration;

import com.ks.kontakt.model.TemperatureMeasurement;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

@TestConfiguration
public class TestConfig {
    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Bean
    public ConsumerFactory<String, TemperatureMeasurement> consumerFactory() {
        Map<String, Object> configProps = KafkaTestUtils.consumerProps("test-consumer-group", "true", embeddedKafkaBroker);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }
}
