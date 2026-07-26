package com.joshsoll.telemetry.platform.organizationmembership.exceptions;

import java.util.UUID;

public class OrganizationMembershipNotFoundException extends RuntimeException {
    public OrganizationMembershipNotFoundException(UUID organizationMembershipId) {
        super("Organization membership not found: " + organizationMembershipId);
    }
}
