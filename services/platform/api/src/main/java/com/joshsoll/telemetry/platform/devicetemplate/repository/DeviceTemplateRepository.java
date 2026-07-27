package com.joshsoll.telemetry.platform.devicetemplate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

public interface DeviceTemplateRepository extends JpaRepository<DeviceTemplate, UUID> {
    boolean existsByOrganizationAndName(Organization organization, String name);
}
