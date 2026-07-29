package com.joshsoll.telemetry.platform.seed.metricdefinition;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.metricdefinition.MetricDataType;
import com.joshsoll.telemetry.platform.metricdefinition.entity.MetricDefinition;
import com.joshsoll.telemetry.platform.metricdefinition.repository.MetricDefinitionRepository;

@Component
public class MetricDefinitionGenerator {

    private final MetricDefinitionRepository metricDefinitionRepository;

    public MetricDefinitionGenerator(
            MetricDefinitionRepository metricDefinitionRepository) {
        this.metricDefinitionRepository = metricDefinitionRepository;
    }

    public MetricDefinition generate(
            String name,
            String description,
            String incomingFieldName,
            MetricDataType dataType,
            String unit,
            DeviceTemplate deviceTemplate) {

        return metricDefinitionRepository
                .findByDeviceTemplateAndIncomingFieldName(
                        deviceTemplate,
                        incomingFieldName)
                .orElseGet(() -> {

                    Instant now = Instant.now();

                    MetricDefinition metricDefinition = new MetricDefinition(
                            name,
                            description,
                            incomingFieldName,
                            dataType,
                            unit,
                            deviceTemplate,
                            now,
                            now);

                    return metricDefinitionRepository.save(metricDefinition);
                });
    }

    public void generate(
            int count,
            DeviceTemplate deviceTemplate) {

        for (int i = 1; i <= count; i++) {
            generate(
                    "Metric Definition " + i,
                    "Metric Definition Description " + i,
                    "metric_" + i,
                    MetricDataType.NUMBER,
                    "unit-" + i,
                    deviceTemplate);
        }
    }
}
