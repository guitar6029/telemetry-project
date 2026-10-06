package com.joshsoll.telemetry.platform.device.importer.dto;

import java.util.List;

public record DeviceImportParseResult(
        List<PreparedDeviceImportRow> validRows,
        List<DeviceImportError> errors) {

}
