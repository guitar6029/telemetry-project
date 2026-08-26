package com.joshsoll.telemetry.platform.device.importer.dto;

import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;

public record DeviceImportResponse(
        String message,
        DeviceImportStatus status) {

}
