package com.joshsoll.telemetry.platform.device.importer.dto;

import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;

public record DeviceImportHistoryResponse(
        UUID importId,
        UUID organizationId,
        UUID templateId,
        UUID hierarchyNodeId,
        String filename,
        DeviceImportMode importMode,
        DeviceImportStatus status,
        Instant submittedAt,
        Instant startedAt,
        Instant completedAt) {
}
