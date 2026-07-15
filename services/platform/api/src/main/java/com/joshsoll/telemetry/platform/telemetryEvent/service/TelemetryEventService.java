package com.joshsoll.telemetry.platform.telemetryEvent.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import com.joshsoll.telemetry.platform.telemetryEvent.dto.CreateTelemetryEventRequest;
import com.joshsoll.telemetry.platform.telemetryEvent.dto.TelemetryEventResponse;
import com.joshsoll.telemetry.platform.telemetryEvent.entity.TelemetryEvent;
import com.joshsoll.telemetry.platform.telemetryEvent.repository.TelemetryEventRepository;

@Service
public class TelemetryEventService {

    private final TelemetryEventRepository telemetryEventRepository;
    private final DeviceRepository deviceRepository;

    public TelemetryEventService(
            TelemetryEventRepository telemetryEventRepository,
            DeviceRepository deviceRepository) {
        this.telemetryEventRepository = telemetryEventRepository;
        this.deviceRepository = deviceRepository;
    }

    public TelemetryEventResponse createTelemetryEvent(CreateTelemetryEventRequest request) {

        Instant now = Instant.now();

        Device device = deviceRepository.findById(request.getDeviceId()).orElseThrow();

        TelemetryEvent telemetryEvent = new TelemetryEvent(
                device,
                request.getRawPayload(),
                now,
                now);

        TelemetryEvent savedTelemetryEvent = telemetryEventRepository.save(telemetryEvent);

        return toResponse(savedTelemetryEvent);
    }

    private TelemetryEventResponse toResponse(TelemetryEvent telemetryEvent) {
        return new TelemetryEventResponse(

                telemetryEvent.getId(),

                telemetryEvent.getDeviceId(),

                telemetryEvent.getCreatedAt(),

                telemetryEvent.getReceivedAt());
    }
}
