package com.joshsoll.telemetry.platform.invitation.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.joshsoll.telemetry.platform.invitation.constants.InvitationConstants;
import com.joshsoll.telemetry.platform.invitation.enums.InvitationStatus;
import com.joshsoll.telemetry.platform.organizationmembership.enums.OrganizationRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "invitations")
public class Invitation {

    @Id
    @Generated
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    protected Invitation() {
    }

    public Invitation(
            UUID organizationId,
            String email,
            OrganizationRole role,
            InvitationStatus status) {

        Instant now = Instant.now();

        this.organizationId = organizationId;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = now;
        this.expiresAt = now.plus(
                InvitationConstants.EXPIRATION_DAYS,
                ChronoUnit.DAYS);
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getEmail() {
        return email;
    }

    public OrganizationRole getRole() {
        return role;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
