package com.joshsoll.telemetry.platform.device.entity;

import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.organization.entity.Organization;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String model;
    private String serialNumber;

    private Organization organization;

    private Instant createdAt;
    private Instant updatedAt;

    protected Device() {
    }

    // we could get crazy with
    // the model class later
    // later add lastUpdatedBy when we have TeamMemmber/ User class udner the
    // Organization
    public Device(String name, String model, String serialNumber, Organization organization, Instant createdAt,
            Instant updatedAt) {
        this.name = name;
        this.model = model;
        this.serialNumber = serialNumber;
        this.organization = organization;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
