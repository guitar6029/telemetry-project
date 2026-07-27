package com.joshsoll.telemetry.platform.seed.telemetryevent;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.telemetryevent.entity.TelemetryEvent;
import com.joshsoll.telemetry.platform.telemetryevent.repository.TelemetryEventRepository;

@Component
public class TelemetryEventGenerator {

    private final TelemetryEventRepository telemetryEventRepository;

    public TelemetryEventGenerator(
            TelemetryEventRepository telemetryEventRepository) {
        this.telemetryEventRepository = telemetryEventRepository;
    }

    public TelemetryEvent generate(
            Device device,
            String rawPayload) {

        Instant now = Instant.now();

        TelemetryEvent telemetryEvent = new TelemetryEvent(
                device,
                rawPayload,
                now,
                now);

        return telemetryEventRepository.save(telemetryEvent);
    }

    public void generate(
            int count,
            Device device) {

        for (int i = 1; i <= count; i++) {
            generate(
                    device,
                    "{\"value\":" + i + "}");
        }
    }
}
