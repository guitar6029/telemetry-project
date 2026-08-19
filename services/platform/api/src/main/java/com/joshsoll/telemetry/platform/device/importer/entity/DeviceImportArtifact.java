package com.joshsoll.telemetry.platform.device.importer.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "device_import_artifacts")
public class DeviceImportArtifact {

    @Id
    @Generated
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_import_id", nullable = false, unique = true)
    private DeviceImport deviceImport;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] content;

    private Instant createdAt;

    protected DeviceImportArtifact() {
    }

    public DeviceImportArtifact(
            DeviceImport deviceImport,
            String fileName,
            String contentType,
            byte[] content) {

        this.deviceImport = deviceImport;
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public DeviceImport getDeviceImport() {
        return deviceImport;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
