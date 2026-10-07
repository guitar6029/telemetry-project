package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportHistoryResponse;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.device.importer.exception.DeviceImportNotFoundException;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportRepository;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@ExtendWith(MockitoExtension.class)
class DeviceImportHistoryServiceTest {

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private DeviceImportRepository deviceImportRepository;

    private DeviceImportHistoryService historyService;
    private User user;
    private Organization organization;
    private UUID organizationId;

    @BeforeEach
    void setUp() {
        historyService = new DeviceImportHistoryService(authorizationService, deviceImportRepository);
        user = mock(User.class);
        organization = mock(Organization.class);
        organizationId = UUID.randomUUID();
        when(organization.getId()).thenReturn(organizationId);
        when(authorizationService.requireOrganizationAccess(user, organizationId)).thenReturn(organization);
    }

    @Test
    void returnsPagedOrganizationImportsNewestFirst() {
        DeviceImport olderImport = importRecord(organization, Instant.parse("2026-01-01T00:00:00Z"));
        DeviceImport newerImport = importRecord(organization, Instant.parse("2026-01-02T00:00:00Z"));
        when(deviceImportRepository.findByOrganization_Id(any(UUID.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(newerImport, olderImport), Pageable.ofSize(2), 3));

        var response = historyService.getImports(user, organizationId, 1, 2);

        assertEquals(2, response.getData().size());
        assertEquals(newerImport.getId(), response.getData().get(0).importId());
        assertEquals(olderImport.getId(), response.getData().get(1).importId());
        assertEquals(1, response.getPage());
        assertEquals(2, response.getSize());
        assertEquals(3, response.getTotal());
        assertEquals(2, response.getTotalPages());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(deviceImportRepository).findByOrganization_Id(eq(organizationId), pageableCaptor.capture());
        Sort.Order order = pageableCaptor.getValue().getSort().getOrderFor("submittedAt");
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    @Test
    void returnsDetailOnlyWhenImportBelongsToRequestedOrganization() {
        DeviceImport deviceImport = importRecord(organization, Instant.parse("2026-01-02T00:00:00Z"));
        when(deviceImportRepository.findByIdAndOrganization_Id(deviceImport.getId(), organizationId))
                .thenReturn(Optional.of(deviceImport));

        DeviceImportHistoryResponse response = historyService.getImport(user, organizationId, deviceImport.getId());

        assertEquals(deviceImport.getId(), response.importId());
        assertEquals(organizationId, response.organizationId());
        assertEquals(deviceImport.getDeviceTemplate().getId(), response.templateId());
        assertEquals(deviceImport.getHierarchyNode().getId(), response.hierarchyNodeId());
        assertEquals("devices.csv", response.filename());
        assertEquals(DeviceImportMode.SKIP_EXISTING, response.importMode());
        assertEquals(DeviceImportStatus.QUEUED, response.status());
    }

    @Test
    void returnsNotFoundForImportOwnedByAnotherOrganization() {
        UUID importId = UUID.randomUUID();
        when(deviceImportRepository.findByIdAndOrganization_Id(importId, organizationId))
                .thenReturn(Optional.empty());

        assertThrows(DeviceImportNotFoundException.class,
                () -> historyService.getImport(user, organizationId, importId));
        verify(deviceImportRepository).findByIdAndOrganization_Id(importId, organizationId);
    }

    @Test
    void returnsNotFoundForMissingImport() {
        UUID importId = UUID.randomUUID();
        when(deviceImportRepository.findByIdAndOrganization_Id(importId, organizationId))
                .thenReturn(Optional.empty());

        assertThrows(DeviceImportNotFoundException.class,
                () -> historyService.getImport(user, organizationId, importId));
    }

    @Test
    void scopesHistoryQueryToRequestedOrganization() {
        Organization anotherOrganization = mock(Organization.class);
        DeviceImport ownImport = importRecord(organization, Instant.parse("2026-01-02T00:00:00Z"));
        DeviceImport anotherOrganizationsImport = importRecord(
                anotherOrganization, Instant.parse("2026-01-03T00:00:00Z"));
        when(deviceImportRepository.findByOrganization_Id(any(UUID.class), any(Pageable.class)))
                .thenAnswer(invocation -> invocation.getArgument(0).equals(organizationId)
                        ? new PageImpl<>(List.of(ownImport))
                        : new PageImpl<>(List.of(anotherOrganizationsImport)));

        var response = historyService.getImports(user, organizationId, 0, 20);

        assertEquals(1, response.getData().size());
        assertEquals(ownImport.getId(), response.getData().get(0).importId());
        verify(deviceImportRepository).findByOrganization_Id(eq(organizationId), any(Pageable.class));
    }

    private DeviceImport importRecord(Organization importOrganization, Instant submittedAt) {
        DeviceTemplate template = mock(DeviceTemplate.class);
        HierarchyNode hierarchyNode = mock(HierarchyNode.class);
        lenient().when(template.getId()).thenReturn(UUID.randomUUID());
        lenient().when(hierarchyNode.getId()).thenReturn(UUID.randomUUID());
        return new DeviceImport(
                importOrganization,
                template,
                hierarchyNode,
                "devices.csv",
                DeviceImportMode.SKIP_EXISTING,
                submittedAt);
    }
}
