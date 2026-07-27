package com.joshsoll.telemetry.platform.telemetryevent.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.device.exception.DeviceNotFoundException;
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import com.joshsoll.telemetry.platform.telemetryevent.dto.CreateTelemetryEventRequest;
import com.joshsoll.telemetry.platform.telemetryevent.dto.TelemetryEventResponse;
import com.joshsoll.telemetry.platform.telemetryevent.entity.TelemetryEvent;
import com.joshsoll.telemetry.platform.telemetryevent.repository.TelemetryEventRepository;

@Service
public class TelemetryEventService {

    private final TelemetryEventRepository telemetryEventRepository;
    private final TelemetryProcessingService telemetryProcessingService;
    private final DeviceRepository deviceRepository;

    public TelemetryEventService(

            TelemetryEventRepository telemetryEventRepository,

            TelemetryProcessingService telemetryProcessingService,

            DeviceRepository deviceRepository) {
        this.telemetryEventRepository = telemetryEventRepository;
        this.deviceRepository = deviceRepository;
        this.telemetryProcessingService = telemetryProcessingService;
    }

    public TelemetryEventResponse createTelemetryEvent(CreateTelemetryEventRequest request) {

        Instant now = Instant.now();

        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new DeviceNotFoundException(request.getDeviceId()));

        TelemetryEvent telemetryEvent = new TelemetryEvent(
                device,
                request.getRawPayload(),
                now,
                now);
        // Persist the parent first
        TelemetryEvent savedTelemetryEvent = telemetryEventRepository.save(telemetryEvent);

        // Now process using the persisted entity
        telemetryProcessingService.processTelemetryEvent(savedTelemetryEvent);

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
