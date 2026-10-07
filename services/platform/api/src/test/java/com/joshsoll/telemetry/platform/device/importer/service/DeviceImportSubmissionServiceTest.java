package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.entity.OutboxMessage;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportRepository;
import com.joshsoll.telemetry.platform.device.importer.repository.OutboxMessageRepository;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class DeviceImportSubmissionServiceTest {

    @Mock
    private DeviceImportRepository deviceImportRepository;

    @Mock
    private OutboxMessageRepository outboxMessageRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DeviceImportSubmissionService submissionService;

    private Organization organization;
    private DeviceTemplate template;
    private HierarchyNode hierarchyNode;
    private DeviceImportContext context;
    private UUID organizationId;
    private UUID templateId;
    private UUID hierarchyNodeId;

    @BeforeEach
    void setUp() {
        organization = mock(Organization.class);
        template = mock(DeviceTemplate.class);
        hierarchyNode = mock(HierarchyNode.class);
        organizationId = UUID.randomUUID();
        templateId = UUID.randomUUID();
        hierarchyNodeId = UUID.randomUUID();
        when(organization.getId()).thenReturn(organizationId);
        when(template.getId()).thenReturn(templateId);
        when(hierarchyNode.getId()).thenReturn(hierarchyNodeId);
        context = new DeviceImportContext(organization, template, hierarchyNode);
        submissionService = new DeviceImportSubmissionService(
                deviceImportRepository, outboxMessageRepository, objectMapper);
    }

    @Test
    void persistsImportAndOutboxTogetherWithMessagePayload() throws Exception {
        byte[] csvData = "a,b,c".getBytes(StandardCharsets.UTF_8);
        DeviceImport deviceImport = submissionService.createImportAndOutboxMessage(
                context, "devices.csv", DeviceImportMode.UPDATE_EXISTING, csvData);

        ArgumentCaptor<DeviceImport> importCaptor = ArgumentCaptor.forClass(DeviceImport.class);
        ArgumentCaptor<OutboxMessage> outboxCaptor = ArgumentCaptor.forClass(OutboxMessage.class);
        InOrder order = inOrder(deviceImportRepository, outboxMessageRepository);
        order.verify(deviceImportRepository).save(importCaptor.capture());
        order.verify(outboxMessageRepository).save(outboxCaptor.capture());

        DeviceImport savedImport = importCaptor.getValue();
        OutboxMessage outboxMessage = outboxCaptor.getValue();
        DeviceImportMessage payload = objectMapper.readValue(
                outboxMessage.getPayload(), DeviceImportMessage.class);

        assertEquals(deviceImport.getId(), savedImport.getId());
        assertEquals(DeviceImportStatus.QUEUED, savedImport.getStatus());
        assertNotNull(savedImport.getSubmittedAt());
        assertEquals(DeviceImportConstants.DEVICE_IMPORT_OUTBOX_EVENT_TYPE, outboxMessage.getEventType());
        assertEquals(savedImport.getId(), outboxMessage.getAggregateId());
        assertEquals(savedImport.getId(), payload.importId());
        assertEquals(organizationId, payload.organizationId());
        assertEquals(templateId, payload.templateId());
        assertEquals(hierarchyNodeId, payload.hierarchyNodeId());
        assertEquals(DeviceImportMode.UPDATE_EXISTING, payload.importMode());
        assertArrayEquals(csvData, payload.csvData());
        assertEquals(0, outboxMessage.getCreatedAt().compareTo(savedImport.getSubmittedAt()));
        assertNull(outboxMessage.getPublishedAt());
        assertNotEquals(savedImport.getId(), outboxMessage.getId());
        assertNotNull(DeviceImportSubmissionService.class
                .getMethod("createImportAndOutboxMessage", DeviceImportContext.class, String.class,
                        DeviceImportMode.class, byte[].class)
                .getAnnotation(Transactional.class));
    }

    @Test
    void rollsBackImportWhenOutboxPersistenceFails() {
        when(outboxMessageRepository.save(any(OutboxMessage.class)))
                .thenThrow(new IllegalStateException("outbox write failed"));

        assertThrows(IllegalStateException.class, () -> submissionService.createImportAndOutboxMessage(
                context,
                "devices.csv",
                DeviceImportMode.SKIP_EXISTING,
                new byte[] { 1, 2, 3 }));

        verify(deviceImportRepository).save(any(DeviceImport.class));
        verify(outboxMessageRepository).save(any(OutboxMessage.class));
    }
}
