package com.joshsoll.telemetry.platform.metricDefinition.dto;

import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.metricDefinition.MetricDataType;

public record MetricDefinitionResponse(
                UUID id,
                String name,
                String description,
                String incomingFieldName,
                MetricDataType dataType,
                String unit,
                UUID deviceTemplateId,
                Instant createdAt,
                Instant updatedAt) {

}
