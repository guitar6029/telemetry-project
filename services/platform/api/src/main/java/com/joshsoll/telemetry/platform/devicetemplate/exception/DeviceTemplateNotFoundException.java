package com.joshsoll.telemetry.platform.devicetemplate.exception;

import java.util.UUID;

public class DeviceTemplateNotFoundException extends RuntimeException {
    public DeviceTemplateNotFoundException(UUID deviceTemplateId) {
        super("Device template not found : " + deviceTemplateId);
    }
}
