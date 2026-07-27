package com.joshsoll.telemetry.platform.seed.metricvalue;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.metricdefinition.entity.MetricDefinition;
import com.joshsoll.telemetry.platform.metricvalue.entity.MetricValue;
import com.joshsoll.telemetry.platform.metricvalue.repository.MetricValueRepository;
import com.joshsoll.telemetry.platform.telemetryevent.entity.TelemetryEvent;

@Component
public class MetricValueGenerator {

    private final MetricValueRepository metricValueRepository;

    public MetricValueGenerator(
            MetricValueRepository metricValueRepository) {
        this.metricValueRepository = metricValueRepository;
    }

    public MetricValue generate(
            TelemetryEvent telemetryEvent,
            MetricDefinition metricDefinition,
            BigDecimal numberValue,
            Boolean booleanValue,
            String stringValue) {

        Instant now = Instant.now();

        MetricValue metricValue = new MetricValue(
                telemetryEvent,
                metricDefinition,
                numberValue,
                booleanValue,
                stringValue,
                now);

        return metricValueRepository.save(metricValue);
    }

    public void generate(
            int count,
            TelemetryEvent telemetryEvent,
            MetricDefinition metricDefinition) {

        for (int i = 1; i <= count; i++) {
            generate(
                    telemetryEvent,
                    metricDefinition,
                    BigDecimal.valueOf(i),
                    null,
                    null);
        }
    }
}
