package com.joshsoll.telemetry.platform.devicetemplate.dto;

import java.util.List;

import com.joshsoll.telemetry.platform.devicetemplate.constants.DeviceTemplateConstants;
import com.joshsoll.telemetry.platform.metricdefinition.dto.CreateTemplateMetricDefinitionRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CreateDeviceTemplateRequest {

    @NotBlank
    @Size(min = DeviceTemplateConstants.NAME_MIN_LENGTH, max = DeviceTemplateConstants.NAME_MAX_LENGTH)
    private String name;

    private String description;

    @NotEmpty
    private List<@Valid CreateTemplateMetricDefinitionRequest> metricDefinitions;

    public CreateDeviceTemplateRequest() {
    }

    public CreateDeviceTemplateRequest(
            String name,
            String description,
            List<CreateTemplateMetricDefinitionRequest> metricDefinitions) {
        this.name = name;
        this.description = description;
        this.metricDefinitions = metricDefinitions;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<CreateTemplateMetricDefinitionRequest> getMetricDefinitions() {
        return this.metricDefinitions;
    }

}
