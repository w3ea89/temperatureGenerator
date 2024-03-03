package com.ks.kontakt.recruitment.temperatureGenerator.generator.configuration;

import org.springframework.stereotype.Component;

@Component
public class TemperatureConfigurationProvider {

    private final double anomalyChance;
    private final double normalRangeMin;
    private final double normalRangeMax;
    private final double anomalyRangeMin;
    private final double anomalyRangeMax;

    public TemperatureConfigurationProvider(double anomalyChance,
                                            double normalRangeMin,
                                            double normalRangeMax,
                                            double anomalyRangeMin,
                                            double anomalyRangeMax) {
        this.anomalyChance = anomalyChance;
        this.normalRangeMin = normalRangeMin;
        this.normalRangeMax = normalRangeMax;
        this.anomalyRangeMin = anomalyRangeMin;
        this.anomalyRangeMax = anomalyRangeMax;
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
}
