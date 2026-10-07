package com.joshsoll.telemetry.platform.device.importer.dto;

import java.util.UUID;

import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;

public record DeviceImportMessage(
        UUID importId,
        UUID organizationId,
        UUID templateId,
        UUID hierarchyNodeId,
        DeviceImportMode importMode,
        byte[] csvData) {

}
