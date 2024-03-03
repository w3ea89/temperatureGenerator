package com.ks.kontakt.recruitment.temperatureGenerator.generator.configuration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class TemperatureConfigurationProviderTest {
    TemperatureConfigurationProvider provider = new TemperatureConfigurationProvider(0.1,
            15.0, 25.0, 29.0, 35.0);

    @Test
    public void testAnomalyChance() {

        assertThat(provider.getAnomalyChance()).isEqualTo(0.1);
    }

    @Test
    public void testNormalRangeMin() {
        assertThat(provider.getNormalRangeMin()).isEqualTo(15.0);
    }

    @Test
    public void testNormalRangeMax() {
        assertThat(provider.getNormalRangeMax()).isEqualTo(25.0);
    }

    @Test
    public void testAnomalyRangeMin() {
        assertThat(provider.getAnomalyRangeMin()).isEqualTo(29.0);
    }

    @Test
    public void testAnomalyRangeMax() {
        assertThat(provider.getAnomalyRangeMax()).isEqualTo(35.0);
    }
}
