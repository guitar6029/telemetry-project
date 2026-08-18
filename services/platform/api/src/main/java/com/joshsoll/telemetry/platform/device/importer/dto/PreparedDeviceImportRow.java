package com.joshsoll.telemetry.platform.device.importer.dto;

import com.joshsoll.telemetry.platform.device.DeviceStatus;

public record PreparedDeviceImportRow(
        String name,
        String manufacturer,
        String model,
        String serialNumber,
        String firmwareVersion,
        DeviceStatus status) {

}
