package com.joshsoll.telemetry.platform.device.importer.exception;

import java.util.UUID;

public class DeviceImportNotFoundException extends RuntimeException {

    public DeviceImportNotFoundException(UUID importId) {
        super("Device import not found: " + importId);
    }
}
