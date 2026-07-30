package com.joshsoll.telemetry.platform.invitation.dto;

import com.joshsoll.telemetry.platform.organizationmembership.enums.OrganizationRole;

public record InvitationRequest(
                String email,
                OrganizationRole role) {

}
