package com.joshsoll.telemetry.platform.metricdefinition.dto;

import com.joshsoll.telemetry.platform.metricdefinition.MetricDataType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTemplateMetricDefinitionRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String incomingFieldName;

    @NotNull
    private MetricDataType dataType;

    private String description;

    private String unit;

    public CreateTemplateMetricDefinitionRequest(
            String name,
            String incomingFieldName,
            MetricDataType dataType,
            String description,
            String unit) {
        this.name = name;
        this.incomingFieldName = incomingFieldName;
        this.dataType = dataType;
        this.description = description;
        this.unit = unit;

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
