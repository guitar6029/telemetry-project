package com.joshsoll.telemetry.platform.device.importer.service;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportResponse;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.device.importer.exception.DeviceImportFileReadException;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Service
public class DeviceImportService {

    private final AuthorizationService authorizationService;

    private final RabbitTemplate rabbitTemplate;
    private final DeviceImportContextService deviceImportContextService;
    private final DeviceImportRepository deviceImportRepository;

    public DeviceImportService(
            AuthorizationService authorizationService,
            RabbitTemplate rabbitTemplate,
            DeviceImportContextService deviceImportContextService,
            DeviceImportRepository deviceImportRepository) {
        this.authorizationService = authorizationService;
        this.rabbitTemplate = rabbitTemplate;
        this.deviceImportContextService = deviceImportContextService;
        this.deviceImportRepository = deviceImportRepository;
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

        if (file.getSize() > DeviceImportConstants.MAX_FILE_SIZE_BYTES) {
            throw new DeviceImportInvalidException("Import file must not exceed 5 MB.");
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
            MultipartFile file,
            DeviceImportMode importMode) {

        if (importMode == null) {
            throw new DeviceImportInvalidException("Import mode is required.");
        }

        DeviceImportContext context = validateImportContext(
                authenticatedUser,
                organizationId,
                templateId,
                hierarchyNodeId,
                file);

        try {
            byte[] csvData = file.getBytes();
            DeviceImport importOperation = new DeviceImport(
                    context.organization(),
                    context.deviceTemplate(),
                    context.hierarchyNode(),
                    file.getOriginalFilename(),
                    importMode,
                    Instant.now());
            deviceImportRepository.save(importOperation);

            DeviceImportMessage message = new DeviceImportMessage(
                    importOperation.getId(),
                    context.organization().getId(),
                    context.deviceTemplate().getId(),
                    context.hierarchyNode().getId(),
                    importMode,
                    csvData);

            rabbitTemplate.convertAndSend(
                    DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME,
                    message);

            return new DeviceImportResponse(
                    importOperation.getId(),
                    "Import job accepted",
                    DeviceImportStatus.QUEUED);
        } catch (IOException exception) {
            throw new DeviceImportFileReadException(
                    "Unable to read import file",
                    exception);
        }
    }

}
