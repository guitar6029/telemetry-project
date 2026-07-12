package com.joshsoll.telemetry.platform.device.entity;

import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.device.DeviceStatus;
import com.joshsoll.telemetry.platform.deviceTemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String firmwareVersion;

    @ManyToOne
    private DeviceTemplate deviceTemplate;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private HierarchyNode hierarchyNode;

    private Instant createdAt;
    private Instant updatedAt;

    protected Device() {
    }

    public Device(
            String name,
            String manufacturer,
            String model,
            String serialNumber,
            String firmwareVersion,
            DeviceStatus status,
            Organization organization,
            HierarchyNode hierarchyNode,
            DeviceTemplate deviceTemplate,
            Instant createdAt,
            Instant updatedAt) {

        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.firmwareVersion = firmwareVersion;
        this.status = status;
        this.organization = organization;
        this.hierarchyNode = hierarchyNode;
        this.deviceTemplate = deviceTemplate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UUID getOrganizationId() {
        return organization.getId();
    }

    public UUID getHierarchyNodeId() {
        return hierarchyNode.getId();
    }

    public UUID getDeviceTemplateId() {
        return deviceTemplate.getId();
    }

}
