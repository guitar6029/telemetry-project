package com.joshsoll.telemetry.platform.device.importer.enums;

public enum DeviceImportStatus {
    QUEUED,
    PROCESSING,
    COMPLETED,
    COMPLETED_WITH_ERRORS,
    FAILED;

    public boolean isTerminal() {
        return this == COMPLETED || this == COMPLETED_WITH_ERRORS;
    }
}
