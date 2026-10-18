package com.joshsoll.telemetry.platform.device.importer.dto;

import java.util.UUID;

public record DeviceImportMessage(
        UUID importId,
        byte[] csvData) {

}
