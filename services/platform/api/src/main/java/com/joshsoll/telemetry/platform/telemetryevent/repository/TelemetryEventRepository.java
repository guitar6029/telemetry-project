package com.joshsoll.telemetry.platform.telemetryevent.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.telemetryevent.entity.TelemetryEvent;

public interface TelemetryEventRepository extends JpaRepository<TelemetryEvent, UUID> {

}
