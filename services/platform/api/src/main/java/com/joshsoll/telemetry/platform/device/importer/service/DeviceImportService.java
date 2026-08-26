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
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateNotFoundException;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateOrganizationMismatchException;
import com.joshsoll.telemetry.platform.devicetemplate.repository.DeviceTemplateRepository;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.hierarchy.exception.HierarchyNodeNotFoundException;
import com.joshsoll.telemetry.platform.hierarchy.exception.HierarchyNodeOrganizationMismatchException;
import com.joshsoll.telemetry.platform.hierarchy.repository.HierarchyNodeRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Service
public class DeviceImportService {

    private final AuthorizationService authorizationService;
    private final DeviceTemplateRepository deviceTemplateRepository;
    private final HierarchyNodeRepository hierarchyNodeRepository;
    private final RabbitTemplate rabbitTemplate;

    public DeviceImportService(
            AuthorizationService authorizationService,
            DeviceTemplateRepository deviceTemplateRepository,
            HierarchyNodeRepository hierarchyNodeRepository,
            RabbitTemplate rabbitTemplate) {
        this.authorizationService = authorizationService;
        this.deviceTemplateRepository = deviceTemplateRepository;
        this.hierarchyNodeRepository = hierarchyNodeRepository;
        this.rabbitTemplate = rabbitTemplate;
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

        DeviceTemplate deviceTemplate = deviceTemplateRepository.findById(templateId)
                .orElseThrow(() -> new DeviceTemplateNotFoundException(templateId));

        HierarchyNode hierarchyNode = hierarchyNodeRepository.findById(hierarchyNodeId)
                .orElseThrow(() -> new HierarchyNodeNotFoundException(hierarchyNodeId));

        if (!deviceTemplate.getOrganizationId().equals(organization.getId())) {
            throw new DeviceTemplateOrganizationMismatchException();
        }

        if (!hierarchyNode.getOrganization().getId().equals(organization.getId())) {
            throw new HierarchyNodeOrganizationMismatchException();
        }

        return new DeviceImportContext(
                organization,
                deviceTemplate,
                hierarchyNode);
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
                    organizationId,
                    templateId,
                    hierarchyNodeId,
                    file.getBytes());

            // Publish the import job to RabbitMQ.
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
