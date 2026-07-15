package com.joshsoll.telemetry.platform.telemetryEvent.dto;

import java.time.Instant;
import java.util.UUID;

public record TelemetryEventResponse(
        UUID id,

        UUID deviceId,

        Instant createdAt,

        Instant receivedAt

) {

}
