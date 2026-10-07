package com.joshsoll.telemetry.platform.device.importer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;

public interface DeviceImportRepository extends JpaRepository<DeviceImport, UUID> {
}
