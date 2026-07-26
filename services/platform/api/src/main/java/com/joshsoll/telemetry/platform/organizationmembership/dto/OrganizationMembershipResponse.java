package com.joshsoll.telemetry.platform.organizationmembership.dto;

import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.organizationmembership.enums.MembershipStatus;
import com.joshsoll.telemetry.platform.organizationmembership.enums.OrganizationRole;

public record OrganizationMembershipResponse(
        UUID id,
        UUID organizatioId,
        UUID userId,
        OrganizationRole role,
        MembershipStatus status,
        Instant createdAt,
        Instant updatedAt) {

}
