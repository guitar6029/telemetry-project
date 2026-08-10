package com.joshsoll.telemetry.platform.metricdefinition.dto;

import java.util.UUID;

import com.joshsoll.telemetry.platform.metricdefinition.MetricDataType;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;

public class UpdateTemplateMetricDefinitionRequest {

    @NotNull
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String incomingFieldName;

    @NotNull
    private MetricDataType dataType;

    private String description;

    private String unit;

    public UpdateTemplateMetricDefinitionRequest(
            UUID id,
            String name,
            String incomingFieldName,
            MetricDataType dataType,
            String description,
            String unit) {
        this.id = id;
        this.name = name;
        this.incomingFieldName = incomingFieldName;
        this.dataType = dataType;
        this.description = description;
        this.unit = unit;

    }

    public UUID getId() {
        return id;
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
}
