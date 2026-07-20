package com.joshsoll.telemetry.platform.deviceTemplate.dto;

import java.util.UUID;

import com.joshsoll.telemetry.platform.deviceTemplate.constants.DeviceTemplateConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateDeviceTemplateRequest {
    @NotBlank
    @Size(min = DeviceTemplateConstants.NAME_MIN_LENGTH, max = DeviceTemplateConstants.NAME_MAX_LENGTH)
    private String name;

    private String description;

    @NotNull
    private UUID organizationId;

    public UpdateDeviceTemplateRequest() {
    }

    public UpdateDeviceTemplateRequest(
            String name,
            String description,
            UUID organizationId) {
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }
}
