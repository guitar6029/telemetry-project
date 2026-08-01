package com.joshsoll.telemetry.platform.organizationmembership.exceptions;

import java.util.UUID;

public class OrganizationMembershipNotFoundException extends RuntimeException {

    public OrganizationMembershipNotFoundException(UUID organizationMembershipId) {
        super("Organization membership not found: " + organizationMembershipId);
    }

    private OrganizationMembershipNotFoundException(String message) {
        super(message);
    }

    public static OrganizationMembershipNotFoundException forUser(UUID userId) {
        return new OrganizationMembershipNotFoundException(
                "User '" + userId + "' does not belong to an organization.");
    }
}
