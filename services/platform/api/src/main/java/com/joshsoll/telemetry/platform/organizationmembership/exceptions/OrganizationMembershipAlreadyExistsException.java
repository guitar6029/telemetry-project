package com.joshsoll.telemetry.platform.organizationmembership.exceptions;

import java.util.UUID;

public class OrganizationMembershipAlreadyExistsException extends RuntimeException {
    public OrganizationMembershipAlreadyExistsException(UUID organizationId, UUID userId) {
        super(
                "Membership already exists for organization "
                        + organizationId
                        + " and user "
                        + userId);
    }
}
