package com.joshsoll.telemetry.platform.device.importer.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

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
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_template_id", nullable = false)
    private DeviceTemplate deviceTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hierarchy_node_id", nullable = false)
    private HierarchyNode hierarchyNode;

    @Column(nullable = false, columnDefinition = "text")
    private String filename;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeviceImportMode importMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeviceImportStatus status;

    @Column(nullable = false)
    private Instant submittedAt;

    private Instant startedAt;
    private Instant completedAt;

    protected DeviceImport() {
    }

    public DeviceImport(
            Organization organization,
            DeviceTemplate deviceTemplate,
            HierarchyNode hierarchyNode,
            String filename,
            DeviceImportMode importMode,
            Instant submittedAt) {
        this.id = UUID.randomUUID();
        this.organization = organization;
        this.deviceTemplate = deviceTemplate;
        this.hierarchyNode = hierarchyNode;
        this.filename = filename == null ? "" : filename;
        this.importMode = importMode;
        this.status = DeviceImportStatus.QUEUED;
        this.submittedAt = submittedAt;
    }

    public UUID getId() {
        return id;
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

    public String getFilename() {
        return filename;
    }

    public DeviceImportMode getImportMode() {
        return importMode;
    }

    public DeviceImportStatus getStatus() {
        return status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void markProcessing(Instant startedAt) {
        this.status = DeviceImportStatus.PROCESSING;
        this.startedAt = startedAt;
        this.completedAt = null;
    }

    public void markCompleted(Instant completedAt) {
        this.status = DeviceImportStatus.COMPLETED;
        this.completedAt = completedAt;
    }

    public void markCompletedWithErrors(Instant completedAt) {
        this.status = DeviceImportStatus.COMPLETED_WITH_ERRORS;
        this.completedAt = completedAt;
    }

    public void markFailed(Instant completedAt) {
        this.status = DeviceImportStatus.FAILED;
        this.completedAt = completedAt;
    }
}
