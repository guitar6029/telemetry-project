package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportProcessingResult;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportRepository;
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@ExtendWith(MockitoExtension.class)
class DeviceImportProcessingServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceImportContextService deviceImportContextService;

    @Mock
    private DeviceImportRepository deviceImportRepository;

    @InjectMocks
    private DeviceImportProcessingService deviceImportProcessingService;

    @Test
    void shouldRejectInvalidCsvWithoutRequeue() {
        UUID organizationId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        UUID hierarchyNodeId = UUID.randomUUID();
        DeviceImportContext context = new DeviceImportContext(
                mock(Organization.class),
                mock(DeviceTemplate.class),
                mock(HierarchyNode.class));
        DeviceImport deviceImport = new DeviceImport(
                context.organization(),
                context.deviceTemplate(),
                context.hierarchyNode(),
                "devices.csv",
                DeviceImportMode.SKIP_EXISTING,
                Instant.now());
        when(deviceImportRepository.findById(deviceImport.getId())).thenReturn(Optional.of(deviceImport));
        List<DeviceImportStatus> savedStatuses = captureSavedStatuses();
        DeviceImportMessage message = new DeviceImportMessage(
                deviceImport.getId(),
                organizationId,
                templateId,
                hierarchyNodeId,
                DeviceImportMode.SKIP_EXISTING,
                ("device_name,vendor,device_model,serial_number\n"
                        + "Temperature Sensor,Acme,TS-1000,TS1000001\n")
                        .getBytes(StandardCharsets.UTF_8));

        when(deviceImportContextService.resolveImportContext(
                organizationId,
                templateId,
                hierarchyNodeId))
                .thenReturn(context);

        AmqpRejectAndDontRequeueException exception = assertThrows(
                AmqpRejectAndDontRequeueException.class,
                () -> deviceImportProcessingService.processImport(message));

        assertInstanceOf(DeviceImportInvalidException.class, exception.getCause());
        assertEquals(DeviceImportStatus.FAILED, deviceImport.getStatus());
        assertNotNull(deviceImport.getStartedAt());
        assertNotNull(deviceImport.getCompletedAt());
        assertEquals(List.of(DeviceImportStatus.PROCESSING, DeviceImportStatus.FAILED), savedStatuses);
        verify(deviceImportContextService).resolveImportContext(
                organizationId,
                templateId,
                hierarchyNodeId);
        verifyNoInteractions(deviceRepository);
    }

    @Test
    void shouldRejectMoreThanTenThousandRowsWithoutProcessingAnyRows() {
        DeviceImportContext context = validContext();
        UUID organizationId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        UUID hierarchyNodeId = UUID.randomUUID();
        when(deviceImportContextService.resolveImportContext(organizationId, templateId, hierarchyNodeId))
                .thenReturn(context);
        DeviceImport deviceImport = prepareImport(context);
        List<DeviceImportStatus> savedStatuses = captureSavedStatuses();

        AmqpRejectAndDontRequeueException exception = assertThrows(
                AmqpRejectAndDontRequeueException.class,
                () -> deviceImportProcessingService.processImport(messageWithRows(
                        deviceImport,
                        organizationId,
                        templateId,
                        hierarchyNodeId,
                        DeviceImportConstants.MAX_ROW_COUNT + 1)));

        assertInstanceOf(DeviceImportInvalidException.class, exception.getCause());
        assertEquals(DeviceImportStatus.FAILED, deviceImport.getStatus());
        assertEquals(List.of(DeviceImportStatus.PROCESSING, DeviceImportStatus.FAILED), savedStatuses);
        verifyNoInteractions(deviceRepository);
    }

    @Test
    void shouldAcceptExactlyTenThousandRowsByRowCountRule() {
        DeviceImportContext context = validContext();
        UUID organizationId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        UUID hierarchyNodeId = UUID.randomUUID();
        when(deviceImportContextService.resolveImportContext(organizationId, templateId, hierarchyNodeId))
                .thenReturn(context);
        DeviceImport deviceImport = prepareImport(context);
        List<DeviceImportStatus> savedStatuses = captureSavedStatuses();

        DeviceImportProcessingResult result = deviceImportProcessingService.processImport(messageWithRows(
                deviceImport,
                organizationId,
                templateId,
                hierarchyNodeId,
                DeviceImportConstants.MAX_ROW_COUNT));

        assertEquals(DeviceImportConstants.MAX_ROW_COUNT, result.totalRows());
        assertEquals(DeviceImportConstants.MAX_ROW_COUNT, result.failedRows());
        assertEquals(DeviceImportStatus.COMPLETED_WITH_ERRORS, deviceImport.getStatus());
        assertNotNull(deviceImport.getCompletedAt());
        assertEquals(
                List.of(DeviceImportStatus.PROCESSING, DeviceImportStatus.COMPLETED_WITH_ERRORS),
                savedStatuses);
    }

    @Test
    void shouldIgnoreDuplicateDeliveryForTerminalImport() {
        DeviceImportContext context = validContext();
        DeviceImport deviceImport = prepareImport(context);
        deviceImport.markCompleted(Instant.now());
        DeviceImportMessage message = messageWithRows(
                deviceImport,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1);

        DeviceImportProcessingResult result = deviceImportProcessingService.processImport(message);

        assertEquals(0, result.totalRows());
        assertEquals(DeviceImportStatus.COMPLETED, deviceImport.getStatus());
        verifyNoInteractions(deviceRepository, deviceImportContextService);
    }

    private DeviceImportContext validContext() {
        return new DeviceImportContext(
                mock(Organization.class),
                mock(DeviceTemplate.class),
                mock(HierarchyNode.class));
    }

    private DeviceImport prepareImport(DeviceImportContext context) {
        DeviceImport deviceImport = new DeviceImport(
                context.organization(),
                context.deviceTemplate(),
                context.hierarchyNode(),
                "devices.csv",
                DeviceImportMode.SKIP_EXISTING,
                Instant.now());
        when(deviceImportRepository.findById(deviceImport.getId())).thenReturn(Optional.of(deviceImport));
        return deviceImport;
    }

    private List<DeviceImportStatus> captureSavedStatuses() {
        List<DeviceImportStatus> statuses = new ArrayList<>();
        doAnswer(invocation -> {
            DeviceImport savedImport = invocation.getArgument(0);
            statuses.add(savedImport.getStatus());
            return savedImport;
        }).when(deviceImportRepository).save(any(DeviceImport.class));
        return statuses;
    }

    private DeviceImportMessage messageWithRows(
            DeviceImport deviceImport,
            UUID organizationId,
            UUID templateId,
            UUID hierarchyNodeId,
            long rowCount) {
        StringBuilder csv = new StringBuilder("name,manufacturer,model,serialnumber,firmwareversion,status\n");
        for (int row = 0; row < rowCount; row++) {
            csv.append(",,,,,\n");
        }
        return new DeviceImportMessage(
                deviceImport.getId(),
                organizationId,
                templateId,
                hierarchyNodeId,
                DeviceImportMode.SKIP_EXISTING,
                csv.toString().getBytes(StandardCharsets.UTF_8));
    }
}
