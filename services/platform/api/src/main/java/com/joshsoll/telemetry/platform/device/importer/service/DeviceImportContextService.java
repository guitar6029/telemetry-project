package com.joshsoll.telemetry.platform.device.importer.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateNotFoundException;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateOrganizationMismatchException;
import com.joshsoll.telemetry.platform.devicetemplate.repository.DeviceTemplateRepository;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.hierarchy.exception.HierarchyNodeNotFoundException;
import com.joshsoll.telemetry.platform.hierarchy.exception.HierarchyNodeOrganizationMismatchException;
import com.joshsoll.telemetry.platform.hierarchy.repository.HierarchyNodeRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.exception.OrganizationNotFoundException;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

@Service
public class DeviceImportContextService {

    private final DeviceTemplateRepository deviceTemplateRepository;
    private final HierarchyNodeRepository hierarchyNodeRepository;
    private final OrganizationRepository organizationRepository;

    public DeviceImportContextService(
            DeviceTemplateRepository deviceTemplateRepository,
            HierarchyNodeRepository hierarchyNodeRepository,
            OrganizationRepository organizationRepository) {
        this.deviceTemplateRepository = deviceTemplateRepository;
        this.hierarchyNodeRepository = hierarchyNodeRepository;
        this.organizationRepository = organizationRepository;
    }

    public DeviceImportContext resolveImportContext(
            UUID organizationId,
            UUID templateId,
            UUID hierarchyNodeId) {

        Organization organization = organizationRepository.findById(
                organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        DeviceTemplate deviceTemplate = deviceTemplateRepository.findById(
                templateId)
                .orElseThrow(() -> new DeviceTemplateNotFoundException(templateId));

        HierarchyNode hierarchyNode = hierarchyNodeRepository.findById(hierarchyNodeId)
                .orElseThrow(() -> new HierarchyNodeNotFoundException(hierarchyNodeId));

        if (!deviceTemplate.getOrganizationId().equals(organizationId)) {
            throw new DeviceTemplateOrganizationMismatchException();
        }

        if (!hierarchyNode.getOrganization().getId().equals(organizationId)) {
            throw new HierarchyNodeOrganizationMismatchException();
        }

        return new DeviceImportContext(
                organization,
                deviceTemplate,
                hierarchyNode);
    }
}
