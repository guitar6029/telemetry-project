package com.joshsoll.telemetry.platform.device.importer.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;

public interface DeviceImportRepository extends JpaRepository<DeviceImport, UUID> {

    Page<DeviceImport> findByOrganization_Id(UUID organizationId, Pageable pageable);

    java.util.Optional<DeviceImport> findByIdAndOrganization_Id(UUID importId, UUID organizationId);
}
