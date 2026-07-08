package com.joshsoll.telemetry.platform.device.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.device.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

}
