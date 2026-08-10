package com.joshsoll.telemetry.platform.devicetemplate.dto;

import java.util.List;

import com.joshsoll.telemetry.platform.devicetemplate.constants.DeviceTemplateConstants;
import com.joshsoll.telemetry.platform.metricdefinition.dto.UpdateTemplateMetricDefinitionRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UpdateDeviceTemplateRequest {
    @NotBlank
    @Size(min = DeviceTemplateConstants.NAME_MIN_LENGTH, max = DeviceTemplateConstants.NAME_MAX_LENGTH)
    private String name;

    private String description;

    @NotEmpty
    private List<@Valid UpdateTemplateMetricDefinitionRequest> metricDefinitions;

    public UpdateDeviceTemplateRequest() {
    }

    public UpdateDeviceTemplateRequest(
            String name,
            String description,
            List<UpdateTemplateMetricDefinitionRequest> metricDefinitions) {
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

    public List<UpdateTemplateMetricDefinitionRequest> getMetricDefinitions() {
        return this.metricDefinitions;
    }

}
