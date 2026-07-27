package com.joshsoll.telemetry.platform.devicetemplate.exception;

public class DuplicateDeviceTemplateNameException extends RuntimeException {
    public DuplicateDeviceTemplateNameException(String name) {
        super("Device template already exists with name " + name + ".");
    }
}
