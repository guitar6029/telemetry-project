package com.joshsoll.telemetry.platform.device.importer.dto;

import java.util.List;

public record DeviceImportProcessingResult(
        long totalRows,
        long createdRows,
        long updatedRows,
        long skippedRows,
        long failedRows,
        List<DeviceImportError> errors) {
}
