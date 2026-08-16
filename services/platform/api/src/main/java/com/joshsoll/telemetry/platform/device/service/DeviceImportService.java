package com.joshsoll.telemetry.platform.device.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Service
public class DeviceImportService {

    private final AuthorizationService authorizationService;

    public DeviceImportService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void importDevices(User authenticatedUser, UUID organizationId, MultipartFile file) {
        Organization organization = authorizationService.requireOrganizationAccess(authenticatedUser, organizationId);

        if (file == null || file.isEmpty()) {
            throw new DeviceImportInvalidException("Import file is required.");
        }

        String contentType = file.getContentType();

        if (!"text/csv".equalsIgnoreCase(contentType)) {
            throw new DeviceImportInvalidException("Import file must be a CSV.");
        }

    }
}
