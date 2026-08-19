package com.joshsoll.telemetry.platform.device.importer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImportArtifact;

public interface DeviceImportArtifactRepository extends JpaRepository<DeviceImportArtifact, UUID> {

}
