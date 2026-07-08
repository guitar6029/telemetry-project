package com.joshsoll.telemetry.platform.device.dto;

import java.time.Instant;
import java.util.UUID;

public record DeviceResponse(
        UUID id,
        String name,
        String model,
        String serialNumber,
        Instant createdAt,
        Instant updatedAt) {

}
