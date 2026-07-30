package com.joshsoll.telemetry.platform.invitation.dto;

import java.time.Instant;

import com.joshsoll.telemetry.platform.organizationmembership.enums.OrganizationRole;

public record InvitationResponse(
                String email,
                OrganizationRole role,
                Instant createdAt) {

}
