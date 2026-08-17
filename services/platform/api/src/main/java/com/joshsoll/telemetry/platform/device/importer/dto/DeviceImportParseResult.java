package com.joshsoll.telemetry.platform.device.importer.dto;

import java.util.List;

import com.joshsoll.telemetry.platform.device.dto.CreateDeviceRequest;

public record DeviceImportParseResult(
        List<CreateDeviceRequest> validRows,
        List<DeviceImportError> errors) {

}
