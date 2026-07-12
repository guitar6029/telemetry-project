package com.joshsoll.telemetry.platform.deviceTemplate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.deviceTemplate.entity.DeviceTemplate;

public interface DeviceTemplateRepository extends JpaRepository<DeviceTemplate, UUID> {
}
