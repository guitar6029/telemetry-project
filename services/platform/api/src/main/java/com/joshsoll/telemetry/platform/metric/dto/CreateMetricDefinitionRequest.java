package com.joshsoll.telemetry.platform.metric.dto;

import java.util.UUID;

import com.joshsoll.telemetry.platform.metric.MetricDataType;
import com.joshsoll.telemetry.platform.metric.constants.MetricDefinitionConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateMetricDefinitionRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Size(min = MetricDefinitionConstants.INCOMING_FIELD_NAME_MIN_LENGTH, max = MetricDefinitionConstants.INCOMING_FIELD_NAME_MAX_LENGTH)
    private String incomingFieldName;

    @NotNull
    private MetricDataType dataType;

    private String description;

    private String unit;

    @NotNull
    private UUID deviceTemplateId;

    public CreateMetricDefinitionRequest() {
    }

    public CreateMetricDefinitionRequest(
            String name,
            String incomingFieldName,
            MetricDataType dataType,
            String description,
            String unit,
            UUID deviceTemplateId) {
        this.name = name;
        this.incomingFieldName = incomingFieldName;
        this.dataType = dataType;
        this.description = description;
        this.unit = unit;
        this.deviceTemplateId = deviceTemplateId;
    }

    public String getName() {
        return name;
    }

    public String getIncomingFieldName() {
        return incomingFieldName;
    }

    public MetricDataType getDataType() {
        return dataType;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public UUID getDeviceTemplateId() {
        return deviceTemplateId;
    }

}
