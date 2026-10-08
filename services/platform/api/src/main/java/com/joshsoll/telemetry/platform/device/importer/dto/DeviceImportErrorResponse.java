package com.joshsoll.telemetry.platform.device.importer.dto;

import java.util.List;

public record DeviceImportErrorResponse(long rowNumber, List<String> messages) { }
