package com.joshsoll.telemetry.platform.device.importer.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportHistoryResponse;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.exception.DeviceImportNotFoundException;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Service
public class DeviceImportHistoryService {

    private final AuthorizationService authorizationService;
    private final DeviceImportRepository deviceImportRepository;

    public DeviceImportHistoryService(
            AuthorizationService authorizationService,
            DeviceImportRepository deviceImportRepository) {
        this.authorizationService = authorizationService;
        this.deviceImportRepository = deviceImportRepository;
    }

    @Transactional(readOnly = true)
    public PagedApiResponse<DeviceImportHistoryResponse> getImports(
            User user,
            UUID organizationId,
            int page,
            int size) {
        Organization organization = authorizationService.requireOrganizationAccess(user, organizationId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"));
        Page<DeviceImportHistoryResponse> imports = deviceImportRepository
                .findByOrganization_Id(organization.getId(), pageable)
                .map(this::toResponse);

        return new PagedApiResponse<>(
                imports.getContent(), "", page, size, imports.getTotalElements(), imports.getTotalPages());
    }

    @Transactional(readOnly = true)
    public DeviceImportHistoryResponse getImport(User user, UUID organizationId, UUID importId) {
        Organization organization = authorizationService.requireOrganizationAccess(user, organizationId);
        DeviceImport deviceImport = deviceImportRepository
                .findByIdAndOrganization_Id(importId, organization.getId())
                .orElseThrow(() -> new DeviceImportNotFoundException(importId));
        return toResponse(deviceImport);
    }

    private DeviceImportHistoryResponse toResponse(DeviceImport deviceImport) {
        return new DeviceImportHistoryResponse(
                deviceImport.getId(),
                deviceImport.getOrganization().getId(),
                deviceImport.getDeviceTemplate().getId(),
                deviceImport.getHierarchyNode().getId(),
                deviceImport.getFilename(),
                deviceImport.getImportMode(),
                deviceImport.getStatus(),
                deviceImport.getSubmittedAt(),
                deviceImport.getStartedAt(),
                deviceImport.getCompletedAt());
    }
}
