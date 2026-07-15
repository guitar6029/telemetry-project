package com.joshsoll.telemetry.platform.telemetryEvent.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.telemetryEvent.dto.CreateTelemetryEventRequest;
import com.joshsoll.telemetry.platform.telemetryEvent.dto.TelemetryEventResponse;
import com.joshsoll.telemetry.platform.telemetryEvent.service.TelemetryEventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryEventController {

    private final TelemetryEventService telemetryEventService;

    public TelemetryEventController(
            TelemetryEventService telemetryEventService) {
        this.telemetryEventService = telemetryEventService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TelemetryEventResponse>> createTelemetryEvent(
            @Valid @RequestBody CreateTelemetryEventRequest request) {
        TelemetryEventResponse telemetryEvent = telemetryEventService.createTelemetryEvent(request);
        ApiResponse<TelemetryEventResponse> response = new ApiResponse<>(telemetryEvent,
                "Telemetry event received successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
