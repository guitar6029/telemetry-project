package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportResponse;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@ExtendWith(MockitoExtension.class)
class DeviceImportServiceTest {

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private DeviceImportContextService deviceImportContextService;

    @Mock
    private DeviceImportSubmissionService deviceImportSubmissionService;

    @InjectMocks
    private DeviceImportService deviceImportService;

    private User user;
    private UUID organizationId;
    private UUID templateId;
    private UUID hierarchyNodeId;
    private Organization organization;
    private DeviceTemplate template;
    private HierarchyNode hierarchyNode;
    private DeviceImportContext context;

    @BeforeEach
    void setUp() {
        user = org.mockito.Mockito.mock(User.class);
        organizationId = UUID.randomUUID();
        templateId = UUID.randomUUID();
        hierarchyNodeId = UUID.randomUUID();
        organization = org.mockito.Mockito.mock(Organization.class);
        template = org.mockito.Mockito.mock(DeviceTemplate.class);
        hierarchyNode = org.mockito.Mockito.mock(HierarchyNode.class);
        context = new DeviceImportContext(organization, template, hierarchyNode);
    }

    @Test
    void rejectsFilesOverFiveMegabytesBeforeResolvingImportContext() {
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(DeviceImportConstants.MAX_FILE_SIZE_BYTES + 1);

        assertThrows(DeviceImportInvalidException.class, () -> deviceImportService.importDevices(
                user, organizationId, templateId, hierarchyNodeId, file, DeviceImportMode.SKIP_EXISTING));

        verify(deviceImportContextService, never()).resolveImportContext(any(), any(), any());
        verify(deviceImportSubmissionService, never()).createImportAndOutboxMessage(
                any(), any(), any(), any());
    }

    @Test
    void queuesCsvWithMoreThanTenThousandRowsWithoutParsingItAtSubmission() {
        prepareContext();
        StringBuilder csv = new StringBuilder("name,manufacturer,model,serialnumber,firmwareversion,status\n");
        for (int row = 0; row <= DeviceImportConstants.MAX_ROW_COUNT; row++) {
            csv.append("name,manufacturer,model,serial-").append(row).append(",1,ACTIVE\n");
        }
        MockMultipartFile file = csvFile(csv.toString());
        DeviceImport importOperation = importOperation(DeviceImportMode.SKIP_EXISTING);
        when(deviceImportSubmissionService.createImportAndOutboxMessage(
                eq(context), eq("devices.csv"), eq(DeviceImportMode.SKIP_EXISTING), any(byte[].class)))
                .thenReturn(importOperation);

        DeviceImportResponse response = deviceImportService.importDevices(
                user, organizationId, templateId, hierarchyNodeId, file, DeviceImportMode.SKIP_EXISTING);

        assertEquals(DeviceImportStatus.QUEUED, response.status());
        assertEquals(importOperation.getId(), response.importId());
        ArgumentCaptor<byte[]> dataCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(deviceImportSubmissionService).createImportAndOutboxMessage(
                eq(context), eq("devices.csv"), eq(DeviceImportMode.SKIP_EXISTING), dataCaptor.capture());
        assertEquals(csv.toString(), new String(dataCaptor.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    void passesSubmissionModeAndContextToTransactionalPersistenceService() {
        prepareContext();
        MockMultipartFile file = csvFile("name,manufacturer,model,serialnumber,firmwareversion,status\n");
        DeviceImport importOperation = importOperation(DeviceImportMode.UPDATE_EXISTING);
        when(deviceImportSubmissionService.createImportAndOutboxMessage(
                eq(context), eq("devices.csv"), eq(DeviceImportMode.UPDATE_EXISTING), any(byte[].class)))
                .thenReturn(importOperation);

        DeviceImportResponse response = deviceImportService.importDevices(
                user, organizationId, templateId, hierarchyNodeId, file, DeviceImportMode.UPDATE_EXISTING);

        assertEquals(DeviceImportStatus.QUEUED, response.status());
        assertEquals(importOperation.getId(), response.importId());
        verify(deviceImportSubmissionService).createImportAndOutboxMessage(
                eq(context), eq("devices.csv"), eq(DeviceImportMode.UPDATE_EXISTING), any(byte[].class));
    }

    private void prepareContext() {
        when(authorizationService.requireOrganizationAccess(user, organizationId)).thenReturn(organization);
        when(organization.getId()).thenReturn(organizationId);
        when(deviceImportContextService.resolveImportContext(organizationId, templateId, hierarchyNodeId))
                .thenReturn(context);
    }

    private DeviceImport importOperation(DeviceImportMode mode) {
        return new DeviceImport(organization, template, hierarchyNode, "devices.csv", mode,
                java.time.Instant.now());
    }

    private MockMultipartFile csvFile(String contents) {
        return new MockMultipartFile(
                "file", "devices.csv", "text/csv", contents.getBytes(StandardCharsets.UTF_8));
    }
}
