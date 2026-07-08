package com.joshsoll.telemetry.platform.device.dto;

import java.util.UUID;

import com.joshsoll.telemetry.platform.device.constants.DeviceConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateDeviceRequest {

    @NotBlank
    @Size(min = DeviceConstants.NAME_MIN_LENGTH, max = DeviceConstants.NAME_MAX_LENGTH)
    private String name;

    // so optional ?
    // @Size(min = DeviceConstants.MODEL_MIN_LENGTH, max =
    // DeviceConstants.MODEL_MAX_LENGTH)
    private String model;
    private String serialNumber;

    @NotNull
    private UUID organizationId;

    public CreateDeviceRequest() {
    }

    public CreateDeviceRequest(String name, String model, String serialNumber, UUID organizationId) {
        this.name = name;
        this.model = model;
        this.serialNumber = serialNumber;
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }
}
