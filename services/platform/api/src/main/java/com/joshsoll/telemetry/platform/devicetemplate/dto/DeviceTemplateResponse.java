package com.joshsoll.telemetry.platform.devicetemplate.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.joshsoll.telemetry.platform.metricdefinition.dto.MetricDefinitionResponse;

public record DeviceTemplateResponse(
        UUID id,
        String name,
        String description,
        UUID organizationId,
        boolean archived,
        List<MetricDefinitionResponse> metricDefinitions,
        Instant createdAt,
        Instant updatedAt) {

}
