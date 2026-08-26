package com.joshsoll.telemetry.platform.device.importer.constants;

import java.util.Set;

public final class DeviceImportConstants {

    public static final long EXPIRATION_MINUTES = 20;
    public static final long PREVIEW_LIMIT = 10;
    public static final Set<String> REQUIRED_HEADERS = Set.of(
            "name",
            "manufacturer",
            "model",
            "serialNumber",
            "firmwareVersion",
            "status");

    public static final String DEVICE_IMPORT_QUEUE_NAME = "device.import";

    protected DeviceImportConstants() {
    }
}
