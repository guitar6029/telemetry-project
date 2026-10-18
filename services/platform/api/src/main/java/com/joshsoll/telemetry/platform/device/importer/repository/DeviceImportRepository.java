package com.joshsoll.telemetry.platform.device.importer.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;

public interface DeviceImportRepository extends JpaRepository<DeviceImport, UUID> {

    Page<DeviceImport> findByOrganization_Id(UUID organizationId, Pageable pageable);

    java.util.Optional<DeviceImport> findByIdAndOrganization_Id(UUID importId, UUID organizationId);

    @Modifying
    @Transactional
    @Query("""
            update DeviceImport deviceImport
            set deviceImport.status = com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus.PROCESSING,
                deviceImport.startedAt = :startedAt,
                deviceImport.completedAt = null
            where deviceImport.id = :importId
              and deviceImport.status = com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus.QUEUED
            """)
    int claimQueuedImport(
            @Param("importId") UUID importId,
            @Param("startedAt") Instant startedAt);
}
