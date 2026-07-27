package com.joshsoll.telemetry.platform.devicetemplate.dto;

import java.time.Instant;
import java.util.UUID;

public record DeviceTemplateResponse(
                UUID id,
                String name,
                String description,
                UUID organizationId,
                boolean archived,
                Instant createdAt,
                Instant updatedAt) {

}
