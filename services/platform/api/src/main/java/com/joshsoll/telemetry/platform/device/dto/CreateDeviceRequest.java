package com.joshsoll.telemetry.platform.device.dto;

import java.util.UUID;

import com.joshsoll.telemetry.platform.device.DeviceStatus;
import com.joshsoll.telemetry.platform.device.constants.DeviceConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateDeviceRequest {

    @NotBlank
    @Size(min = DeviceConstants.NAME_MIN_LENGTH, max = DeviceConstants.NAME_MAX_LENGTH)
    private String name;

    private String model;
    private String serialNumber;
    private String manufacturer;
    private String firmwareVersion;

    private DeviceStatus status;

    @NotNull
    private UUID organizationId;

    @NotNull
    private UUID hierarchyNodeId;

    @NotNull
    private UUID deviceTemplateId;

    public CreateDeviceRequest() {
    }

    public CreateDeviceRequest(
            String name,
            String manufacturer,
            String model,
            String serialNumber,
            String firmwareVersion,
            DeviceStatus status,
            UUID organizationId,
            UUID hierarchyNodeId,
            UUID deviceTemplateId) {

        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.firmwareVersion = firmwareVersion;
        this.status = status;
        this.organizationId = organizationId;
        this.hierarchyNodeId = hierarchyNodeId;
        this.deviceTemplateId = deviceTemplateId;
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

    public UUID getHierarchyNodeId() {
        return hierarchyNodeId;
    }

    public UUID getDeviceTemplateId() {
        return deviceTemplateId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public DeviceStatus getStatus() {
        return status;
    }
}
