package com.joshsoll.telemetry.platform.deviceTemplate.entity;

import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.organization.entity.Organization;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "device_templates")
public class DeviceTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String description;

    @ManyToOne
    private Organization organization;

    private boolean archived;

    private Instant createdAt;
    private Instant updatedAt;

    protected DeviceTemplate() {
    }

    public DeviceTemplate(
            String name,
            String description,
            Organization organization,
            boolean archived,
            Instant createdAt,
            Instant updatedAt) {
        this.name = name;
        this.description = description;
        this.organization = organization;
        this.archived = archived;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isArchived() {
        return archived;
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

}
