package com.joshsoll.telemetry.platform.organization.exception;

public class DuplicateOrganizationSlugException extends RuntimeException {
    public DuplicateOrganizationSlugException(String slug) {
        super("Organization slug already exists: " + slug);
    }
}
