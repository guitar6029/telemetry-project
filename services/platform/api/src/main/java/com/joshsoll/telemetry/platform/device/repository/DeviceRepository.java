package com.joshsoll.telemetry.platform.device.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    boolean existsByOrganizationAndSerialNumber(
            Organization organization,
            String serialNumber);

    Optional<Device> findBySerialNumber(String serialNumber);
}
