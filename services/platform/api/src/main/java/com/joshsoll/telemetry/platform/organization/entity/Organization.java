package com.joshsoll.telemetry.platform.organization.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

import org.hibernate.annotations.Generated;

import com.joshsoll.telemetry.platform.organization.constants.OrganizationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @Generated // <--- Tells Hibernate: "DB generates this, omit from INSERT"
    @ColumnDefault("gen_random_uuid()") // <--- Maps to your SQL DEFAULT
    private UUID id;

    @Column(nullable = false, length = OrganizationConstants.NAME_MAX_LENGTH)
    private String name;

    @Column(nullable = false, unique = true, length = OrganizationConstants.SLUG_MAX_LENGTH)
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