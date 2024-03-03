package com.ks.kontakt.recruitment.temperatureGenerator.generator;

import com.ks.kontakt.model.TemperatureMeasurement;
import com.ks.kontakt.recruitment.temperatureGenerator.generator.configuration.TestConfig;
import com.ks.kontakt.recruitment.temperatureGenerator.generator.sensor.TemperatureReaderSimulator;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@ContextConfiguration(classes = TestConfig.class)
@DirtiesContext
@EmbeddedKafka(topics = {"test-temperatureMeasurements"}, partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TemperatureGeneratorIT {

    @Autowired
    TemperatureReaderSimulator temperatureReaderSimulator;
    @Autowired
    ConsumerFactory<String, TemperatureMeasurement> consumerFactory;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    public void verifyIfGeneratorsProperlySendMeasurementsToKafka() {
        Consumer<String, TemperatureMeasurement> consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "test-temperatureMeasurements");
        temperatureReaderSimulator.stop();
        ConsumerRecords<String, TemperatureMeasurement> received = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5));
        consumer.close();
        assertThat(received.count()).isGreaterThan(1);
        TemperatureMeasurement recordValue = received.iterator().next().value();
        assertThat(recordValue.getThermometerId()).isPositive();
        assertThat(recordValue.getTemperature()).isGreaterThan(14);
    }
}
