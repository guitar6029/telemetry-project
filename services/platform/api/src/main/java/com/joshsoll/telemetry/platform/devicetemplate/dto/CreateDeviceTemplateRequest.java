package com.joshsoll.telemetry.platform.devicetemplate.dto;

import java.util.List;
import java.util.UUID;

import com.joshsoll.telemetry.platform.devicetemplate.constants.DeviceTemplateConstants;
import com.joshsoll.telemetry.platform.metricdefinition.dto.CreateMetricDefinitionRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateDeviceTemplateRequest {

    @NotBlank
    @Size(min = DeviceTemplateConstants.NAME_MIN_LENGTH, max = DeviceTemplateConstants.NAME_MAX_LENGTH)
    private String name;

    private String description;

    @NotNull
    private UUID organizationId;

    @NotEmpty
    @Valid
    private List<CreateMetricDefinitionRequest> metricDefinitions;

    public CreateDeviceTemplateRequest() {
    }

    public CreateDeviceTemplateRequest(
            String name,
            String description,
            UUID organizationId,
            List<CreateMetricDefinitionRequest> metricDefinitions) {
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
        this.metricDefinitions = metricDefinitions;
    }

    public String getName() {
        return name;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getDescription() {
        return description;
    }

    public List<CreateMetricDefinitionRequest> getMetricDefinitions() {
        return this.metricDefinitions;
    }

}
