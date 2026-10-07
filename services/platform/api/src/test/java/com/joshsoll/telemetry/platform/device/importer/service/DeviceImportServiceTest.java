package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportResponse;
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
    private RabbitTemplate rabbitTemplate;

    @Mock
    private DeviceImportContextService deviceImportContextService;

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
        user = mock(User.class);
        organizationId = UUID.randomUUID();
        templateId = UUID.randomUUID();
        hierarchyNodeId = UUID.randomUUID();
        organization = mock(Organization.class);
        template = mock(DeviceTemplate.class);
        hierarchyNode = mock(HierarchyNode.class);
        context = new DeviceImportContext(organization, template, hierarchyNode);

    }

    @Test
    void rejectsFilesOverFiveMegabytesBeforeResolvingImportContext() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(DeviceImportConstants.MAX_FILE_SIZE_BYTES + 1);

        assertThrows(DeviceImportInvalidException.class, () -> deviceImportService.importDevices(
                user, organizationId, templateId, hierarchyNodeId, file, DeviceImportMode.SKIP_EXISTING));

        verify(deviceImportContextService, never()).resolveImportContext(any(), any(), any());
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(Object.class));
    }

    @Test
    void queuesCsvWithMoreThanTenThousandRowsWithoutParsingItAtSubmission() {
        when(authorizationService.requireOrganizationAccess(user, organizationId))
                .thenReturn(organization);
        when(organization.getId()).thenReturn(organizationId);
        when(template.getId()).thenReturn(templateId);
        when(hierarchyNode.getId()).thenReturn(hierarchyNodeId);
        when(deviceImportContextService.resolveImportContext(
                organizationId, templateId, hierarchyNodeId))
                .thenReturn(context);

        StringBuilder csv = new StringBuilder("name,manufacturer,model,serialnumber,firmwareversion,status\n");
        for (int row = 0; row <= DeviceImportConstants.MAX_ROW_COUNT; row++) {
            csv.append("name,manufacturer,model,serial-").append(row).append(",1,ACTIVE\n");
        }
        MockMultipartFile file = csvFile(csv.toString());

        deviceImportService.importDevices(
                user, organizationId, templateId, hierarchyNodeId, file, DeviceImportMode.SKIP_EXISTING);

        ArgumentCaptor<DeviceImportMessage> messageCaptor = ArgumentCaptor.forClass(DeviceImportMessage.class);
        verify(rabbitTemplate).convertAndSend(
                eq(DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME), messageCaptor.capture());
        assertEquals(csv.toString(), new String(messageCaptor.getValue().csvData(), StandardCharsets.UTF_8));
    }

    @Test
    void queuesTenThousandRowsAndPropagatesSelectedMode() {
        when(authorizationService.requireOrganizationAccess(user, organizationId))
                .thenReturn(organization);
        when(organization.getId()).thenReturn(organizationId);
        when(template.getId()).thenReturn(templateId);
        when(hierarchyNode.getId()).thenReturn(hierarchyNodeId);
        when(deviceImportContextService.resolveImportContext(
                organizationId, templateId, hierarchyNodeId))
                .thenReturn(context);

        StringBuilder csv = new StringBuilder("name,manufacturer,model,serialnumber,firmwareversion,status\n");
        for (int row = 0; row < DeviceImportConstants.MAX_ROW_COUNT; row++) {
            csv.append("name,manufacturer,model,serial-").append(row).append(",1,ACTIVE\n");
        }
        MockMultipartFile file = csvFile(csv.toString());

        DeviceImportResponse response = deviceImportService.importDevices(
                user, organizationId, templateId, hierarchyNodeId, file, DeviceImportMode.UPDATE_EXISTING);

        assertEquals(DeviceImportStatus.QUEUED, response.status());
        ArgumentCaptor<DeviceImportMessage> messageCaptor = ArgumentCaptor.forClass(DeviceImportMessage.class);
        verify(rabbitTemplate).convertAndSend(
                eq(DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME), messageCaptor.capture());
        DeviceImportMessage message = messageCaptor.getValue();
        assertEquals(DeviceImportMode.UPDATE_EXISTING, message.importMode());
        assertEquals(organizationId, message.organizationId());
        assertEquals(templateId, message.templateId());
        assertEquals(hierarchyNodeId, message.hierarchyNodeId());
        assertEquals(csv.toString(), new String(message.csvData(), StandardCharsets.UTF_8));
    }

    private MockMultipartFile csvFile(String contents) {
        return new MockMultipartFile(
                "file",
                "devices.csv",
                "text/csv",
                contents.getBytes(StandardCharsets.UTF_8));
    }
}
