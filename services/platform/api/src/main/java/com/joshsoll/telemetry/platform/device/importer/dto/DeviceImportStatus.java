package com.joshsoll.telemetry.platform.device.importer.dto;

public enum DeviceImportStatus {
    PREVIEW,
    QUEUED,
    PROCESSING,
    COMPLETED,
    COMPLETED_WITH_ERRORS,
    FAILED,
    CANCELLED,
    EXPIRED
}
