package com.joshsoll.telemetry.platform.metric.dto;

import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.metric.MetricDataType;

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
