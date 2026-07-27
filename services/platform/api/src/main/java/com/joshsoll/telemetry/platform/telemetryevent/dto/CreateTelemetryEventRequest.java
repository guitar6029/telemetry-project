package com.joshsoll.telemetry.platform.telemetryevent.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTelemetryEventRequest {

    @NotNull
    private UUID deviceId;

    @NotBlank
    private String rawPayload;

    public CreateTelemetryEventRequest() {
    }

    public CreateTelemetryEventRequest(

            UUID deviceId,

            String rawPayload) {
        this.deviceId = deviceId;
        this.rawPayload = rawPayload;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public String getRawPayload() {
        return rawPayload;
    }
}
