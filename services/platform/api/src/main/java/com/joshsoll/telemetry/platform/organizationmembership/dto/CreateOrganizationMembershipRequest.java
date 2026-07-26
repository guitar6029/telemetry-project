package com.joshsoll.telemetry.platform.organizationmembership.dto;

import java.util.UUID;

import com.joshsoll.telemetry.platform.organizationmembership.enums.OrganizationRole;

import jakarta.validation.constraints.NotNull;

public class CreateOrganizationMembershipRequest {

    @NotNull
    private UUID organizationId;

    @NotNull
    private UUID userId;

    @NotNull
    private OrganizationRole role;

    public CreateOrganizationMembershipRequest(
            UUID organizationId,
            UUID userId,
            OrganizationRole role) {
        this.organizationId = organizationId;
        this.userId = userId;
        this.role = role;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public OrganizationRole getRole() {
        return role;
    }
}
