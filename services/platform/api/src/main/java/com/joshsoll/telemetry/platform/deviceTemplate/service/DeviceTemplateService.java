package com.joshsoll.telemetry.platform.deviceTemplate.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.deviceTemplate.dto.CreateDeviceTemplateRequest;
import com.joshsoll.telemetry.platform.deviceTemplate.dto.DeviceTemplateResponse;
import com.joshsoll.telemetry.platform.deviceTemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.deviceTemplate.repository.DeviceTemplateRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

@Service
public class DeviceTemplateService {
    private final DeviceTemplateRepository deviceTemplateRepository;
    private final OrganizationRepository organizationRepository;

    public DeviceTemplateService(DeviceTemplateRepository deviceTemplateRepository,
            OrganizationRepository organizationRepository) {
        this.deviceTemplateRepository = deviceTemplateRepository;
        this.organizationRepository = organizationRepository;
    }

    public DeviceTemplateResponse createDeviceTemplate(CreateDeviceTemplateRequest request) {

        Instant now = Instant.now();

        // Find organization
        Organization organization = organizationRepository.findById(request.getOrganizationId()).orElseThrow();

        // Validate template name uniqueness
        if (deviceTemplateRepository.existsByOrganizationAndName(organization, request.getName())) {
            throw new IllegalArgumentException("Device template already exists with that name");
        }

        DeviceTemplate deviceTemplate = new DeviceTemplate(
                request.getName(),
                request.getDescription(),
                organization,
                false,
                now,
                now);

        DeviceTemplate savedDeviceTemplate = deviceTemplateRepository.save(deviceTemplate);

        return toResponse(savedDeviceTemplate);
    }

    private DeviceTemplateResponse toResponse(DeviceTemplate deviceTemplate) {
        return new DeviceTemplateResponse(
                deviceTemplate.getId(),
                deviceTemplate.getName(),
                deviceTemplate.getDescription(),
                deviceTemplate.getOrganizationId(),
                deviceTemplate.isArchived(),
                deviceTemplate.getCreatedAt(),
                deviceTemplate.getUpdatedAt());
    }
}
