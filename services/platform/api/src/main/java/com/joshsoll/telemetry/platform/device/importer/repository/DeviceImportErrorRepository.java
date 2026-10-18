package com.joshsoll.telemetry.platform.device.importer.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.device.importer.entity.PersistedDeviceImportError;

public interface DeviceImportErrorRepository extends JpaRepository<PersistedDeviceImportError, UUID> {
    Page<PersistedDeviceImportError> findByDeviceImport_Id(UUID importId, Pageable pageable);
    void deleteByDeviceImport_Id(UUID importId);
}
