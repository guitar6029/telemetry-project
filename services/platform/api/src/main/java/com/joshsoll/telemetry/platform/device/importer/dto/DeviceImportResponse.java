package com.joshsoll.telemetry.platform.device.importer.dto;

import java.util.UUID;

import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;

public record DeviceImportResponse(
        UUID importId,
        String message,
        DeviceImportStatus status) {

}
