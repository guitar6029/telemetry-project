package com.joshsoll.telemetry.platform.device.importer.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportResponse;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.device.importer.exception.DeviceImportFileReadException;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Service
public class DeviceImportService {

    private final AuthorizationService authorizationService;

    private final RabbitTemplate rabbitTemplate;
    private final DeviceImportContextService deviceImportContextService;

    public DeviceImportService(
            AuthorizationService authorizationService,
            RabbitTemplate rabbitTemplate,
            DeviceImportContextService deviceImportContextService) {
        this.authorizationService = authorizationService;
        this.rabbitTemplate = rabbitTemplate;
        this.deviceImportContextService = deviceImportContextService;
    }

    public DeviceImportContext validateImportContext(
            User authenticatedUser,
            UUID organizationId,
            UUID templateId,
            UUID hierarchyNodeId,
            MultipartFile file) {

        Organization organization = authorizationService.requireOrganizationAccess(
                authenticatedUser,
                organizationId);

        if (file == null || file.isEmpty()) {
            throw new DeviceImportInvalidException("Import file is required.");
        }

        String contentType = file.getContentType();

        if (!"text/csv".equalsIgnoreCase(contentType)) {
            throw new DeviceImportInvalidException("Import file must be a CSV.");
        }

        return deviceImportContextService.resolveImportContext(
                organization.getId(),
                templateId,
                hierarchyNodeId);

    }

    public DeviceImportResponse importDevices(
            User authenticatedUser,
            UUID organizationId,
            UUID templateId,
            UUID hierarchyNodeId,
            MultipartFile file) {

        DeviceImportContext context = validateImportContext(
                authenticatedUser,
                organizationId,
                templateId,
                hierarchyNodeId,
                file);

        try {
            DeviceImportMessage message = new DeviceImportMessage(
                    context.organization().getId(),
                    context.deviceTemplate().getId(),
                    context.hierarchyNode().getId(),
                    file.getBytes());

            rabbitTemplate.convertAndSend(message);

            return new DeviceImportResponse(
                    "Import job accepted",
                    DeviceImportStatus.QUEUED);
        } catch (IOException exception) {
            throw new DeviceImportFileReadException(
                    "Unable to read import file",
                    exception);
        }

    }
}
