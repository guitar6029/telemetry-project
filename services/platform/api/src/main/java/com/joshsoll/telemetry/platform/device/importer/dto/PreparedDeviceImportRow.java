package com.joshsoll.telemetry.platform.device.importer.dto;

public record PreparedDeviceImportRow(
        long rowNumber,
        String name,
        String manufacturer,
        String model,
        String serialNumber,
        String firmwareVersion,
        String status) {

}
