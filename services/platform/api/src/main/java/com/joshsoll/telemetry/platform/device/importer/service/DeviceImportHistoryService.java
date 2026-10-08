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
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportErrorResponse;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.exception.DeviceImportNotFoundException;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportRepository;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportErrorRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Service
public class DeviceImportHistoryService {

    private static final String SUBMITTED_AT_PROPERTY = "submittedAt";
    private static final String ID_PROPERTY = "id";

    private final AuthorizationService authorizationService;
    private final DeviceImportRepository deviceImportRepository;
    private final DeviceImportErrorRepository deviceImportErrorRepository;

    public DeviceImportHistoryService(
            AuthorizationService authorizationService,
            DeviceImportRepository deviceImportRepository,
            DeviceImportErrorRepository deviceImportErrorRepository) {
        this.authorizationService = authorizationService;
        this.deviceImportRepository = deviceImportRepository;
        this.deviceImportErrorRepository = deviceImportErrorRepository;
    }

    @Transactional(readOnly = true)
    public PagedApiResponse<DeviceImportHistoryResponse> getImports(
            User user,
            UUID organizationId,
            int page,
            int size) {
        Organization organization = authorizationService.requireOrganizationAccess(user, organizationId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.desc(SUBMITTED_AT_PROPERTY),
                Sort.Order.desc(ID_PROPERTY)));
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

    @Transactional(readOnly = true)
    public PagedApiResponse<DeviceImportErrorResponse> getImportErrors(
            User user, UUID organizationId, UUID importId, int page, int size) {
        Organization organization = authorizationService.requireOrganizationAccess(user, organizationId);
        deviceImportRepository.findByIdAndOrganization_Id(importId, organization.getId())
                .orElseThrow(() -> new DeviceImportNotFoundException(importId));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("rowNumber")));
        Page<DeviceImportErrorResponse> errorPage = deviceImportErrorRepository
                .findByDeviceImport_Id(importId, pageable)
                .map(error -> new DeviceImportErrorResponse(error.getRowNumber(), error.getMessages()));
        return new PagedApiResponse<>(errorPage.getContent(), "", page, size,
                errorPage.getTotalElements(), errorPage.getTotalPages());
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
                deviceImport.getCompletedAt(),
                deviceImport.getTotalRows(), deviceImport.getCreatedRows(), deviceImport.getUpdatedRows(),
                deviceImport.getSkippedRows(), deviceImport.getFailedRows());
    }
}
