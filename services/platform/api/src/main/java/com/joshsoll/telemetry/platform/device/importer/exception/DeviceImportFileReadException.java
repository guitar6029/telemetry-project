package com.joshsoll.telemetry.platform.device.importer.exception;

public class DeviceImportFileReadException extends RuntimeException {

    public DeviceImportFileReadException(String message) {
        super(message);
    }

    public DeviceImportFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
