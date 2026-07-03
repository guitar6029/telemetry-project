package com.joshsoll.telemetry.platform.organization.entity;

import java.time.Instant;
import java.util.UUID;

// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.Id;

//later add Entity
public class Organization {

    // @Id
    // @GeneratedValue
    private UUID id;

    private String name;
    private String slug;

    private Instant createdAt;
    private Instant updatedAt;

    protected Organization() {
    }

    public Organization(String name, String slug, Instant createdAt, Instant updatedAt) {
        this.name = name;
        this.slug = slug;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}