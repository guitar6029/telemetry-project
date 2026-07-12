package com.joshsoll.telemetry.platform.device.dto;

import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.device.DeviceStatus;

public record DeviceResponse(
        UUID id,
        String name,
        String manufacturer,
        String model,
        String serialNumber,
        String firmwareVersion,
        DeviceStatus status,
        UUID organizationId,
        UUID hierarchyNodeId,
        UUID deviceTemplateId,
        Instant createdAt,
        Instant updatedAt) {

}
