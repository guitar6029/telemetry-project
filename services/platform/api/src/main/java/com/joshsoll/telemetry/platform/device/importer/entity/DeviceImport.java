package com.joshsoll.telemetry.platform.device.importer.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
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
@Table(name = "device_imports")
public class DeviceImport {

    @Id
    @Generated
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeviceImportStatus status = DeviceImportStatus.PREVIEW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeviceImportMode mode = DeviceImportMode.SKIP_EXISTING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private DeviceTemplate deviceTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hierarchy_node_id", nullable = false)
    private HierarchyNode hierarchyNode;

    @Column(nullable = false)
    private Integer totalRows = 0;

    @Column(nullable = false)
    private Integer validRows = 0;

    @Column(nullable = false)
    private Integer invalidRows = 0;

    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;

    protected DeviceImport() {
    }

    public DeviceImport(
            DeviceImportMode mode,
            Organization organization,
            DeviceTemplate deviceTemplate,
            HierarchyNode hierarchyNode,
            Integer totalRows,
            Integer validRows,
            Integer invalidRows) {

        Instant now = Instant.now();

        this.mode = mode;
        this.organization = organization;
        this.deviceTemplate = deviceTemplate;
        this.hierarchyNode = hierarchyNode;
        this.totalRows = totalRows;
        this.validRows = validRows;
        this.invalidRows = invalidRows;
        this.expiresAt = now.plus(
                Duration.ofMinutes(DeviceImportConstants.EXPIRATION_MINUTES));
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public DeviceImportStatus getStatus() {
        return status;
    }

    public DeviceImportMode getMode() {
        return mode;
    }

    public Organization getOrganization() {
        return organization;
    }

    public DeviceTemplate getDeviceTemplate() {
        return deviceTemplate;
    }

    public HierarchyNode getHierarchyNode() {
        return hierarchyNode;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public Integer getValidRows() {
        return validRows;
    }

    public Integer getInvalidRows() {
        return invalidRows;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
