package com.joshsoll.telemetry.platform.devicetemplate.exception;

public class DeviceTemplateOrganizationMismatchException extends RuntimeException {
    public DeviceTemplateOrganizationMismatchException() {
        super("Hierarchy node does not belong to the organization.");
    }
}
