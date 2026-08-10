package com.joshsoll.telemetry.platform.organization.exception;

import java.util.UUID;

public class OrganizationAccessDeniedException extends RuntimeException {

    public OrganizationAccessDeniedException(UUID organizationId) {
        super("Access denied for organization: " + organizationId);
    }
}
