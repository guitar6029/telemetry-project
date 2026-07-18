package com.joshsoll.telemetry.platform.device.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.joshsoll.telemetry.platform.device.DeviceStatus;
import com.joshsoll.telemetry.platform.device.constants.DeviceConstants;
import com.joshsoll.telemetry.platform.deviceTemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @Generated
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, length = DeviceConstants.NAME_MAX_LENGTH)
    private String name;

    @Column(nullable = false, length = DeviceConstants.MANUFACTURER_MAX_LENGTH)
    private String manufacturer;

    @Column(nullable = false, length = DeviceConstants.MODEL_MAX_LENGTH)
    private String model;

    @Column(nullable = false, length = DeviceConstants.SERIAL_MAX_LENGTH)
    private String serialNumber;

    @Column(nullable = false, length = DeviceConstants.FIRMWARE_VERSION_MAX_LENGTH)
    private String firmwareVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_template_id", nullable = false)
    private DeviceTemplate deviceTemplate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = DeviceConstants.DEVICE_STATUS_MAX_LENGTH)
    private DeviceStatus status = DeviceStatus.OFFLINE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hierarchy_node_id", nullable = false)
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

    public DeviceTemplate getDeviceTemplate() {
        return deviceTemplate;
    }

}
