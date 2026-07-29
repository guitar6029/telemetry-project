package com.joshsoll.telemetry.platform.organizationmembership.dto;

import com.joshsoll.telemetry.platform.organizationmembership.enums.MembershipStatus;
import com.joshsoll.telemetry.platform.organizationmembership.enums.OrganizationRole;

import jakarta.validation.constraints.NotNull;

public record UpdateOrganizationMembershipRequest(
        @NotNull OrganizationRole role,

        @NotNull MembershipStatus status) {
}
