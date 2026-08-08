package com.joshsoll.telemetry.platform.devicesoverview.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import com.joshsoll.telemetry.platform.devicesoverview.dto.DevicesSummaryResponse;
import com.joshsoll.telemetry.platform.devicetemplate.repository.DeviceTemplateRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Service

public class DevicesOverviewService {

    private final AuthorizationService authorizationService;
    private final DeviceTemplateRepository deviceTemplateRepository;
    private final DeviceRepository deviceRepository;

    public DevicesOverviewService(
            AuthorizationService authorizationService,
            DeviceTemplateRepository deviceTemplateRepository,
            DeviceRepository deviceRepository

    ) {
        this.authorizationService = authorizationService;
        this.deviceTemplateRepository = deviceTemplateRepository;
        this.deviceRepository = deviceRepository;
    }

    public DevicesSummaryResponse getDevicesSummary(User authenticatedUser, UUID organizationId) {

        Organization organization = authorizationService.requireOrganizationAccess(authenticatedUser, organizationId);

        long numberOfDeviceTemplates = deviceTemplateRepository.countByOrganization_Id(organization.getId());

        long numberOfDevices = deviceRepository.countByOrganization_Id(organization.getId());

        DevicesSummaryResponse devicesSummaryResponse = new DevicesSummaryResponse(
                numberOfDeviceTemplates,
                numberOfDevices);

        return devicesSummaryResponse;

    }

}
