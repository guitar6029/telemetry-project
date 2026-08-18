package com.joshsoll.telemetry.platform.device.importer.exception;

import java.io.IOException;

public class DeviceImportArtifactException extends RuntimeException {

    public DeviceImportArtifactException(String message, IOException ex) {
        super(message + ": " + ex);
    }

}
