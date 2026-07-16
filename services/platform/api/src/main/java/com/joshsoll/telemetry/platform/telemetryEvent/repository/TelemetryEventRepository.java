package com.joshsoll.telemetry.platform.telemetryEvent.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.telemetryEvent.entity.TelemetryEvent;

public interface TelemetryEventRepository extends JpaRepository<TelemetryEvent, UUID> {

}
