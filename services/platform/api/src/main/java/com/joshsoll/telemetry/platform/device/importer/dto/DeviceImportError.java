package com.joshsoll.telemetry.platform.device.importer.dto;

import java.util.List;

public record DeviceImportError(
        long rowNumber,
        List<String> errors) {
}
