package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.never;
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

    private static final UUID ORGANIZATION_ID = UUID.randomUUID();
    private static final UUID TEMPLATE_ID = UUID.randomUUID();
    private static final UUID HIERARCHY_NODE_ID = UUID.randomUUID();
    private static final String VALID_CSV = "name,manufacturer,model,serialnumber,firmwareversion,status\n"
            + "Temperature Sensor,Acme,TS-1000,TS1000001,1.0,ONLINE\n";

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceImportContextService deviceImportContextService;

    @Mock
    private DeviceImportRepository deviceImportRepository;

    @Mock
    private DeviceImportResultPersistenceService resultPersistenceService;

    @InjectMocks
    private DeviceImportProcessingService deviceImportProcessingService;

    @Test
    void shouldRejectInvalidCsvWithoutRequeue() {
        DeviceImportContext context = validContext();
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
                ("device_name,vendor,device_model,serial_number\n"
                        + "Temperature Sensor,Acme,TS-1000,TS1000001\n")
                        .getBytes(StandardCharsets.UTF_8));

        when(deviceImportContextService.resolveImportContext(
                ORGANIZATION_ID, TEMPLATE_ID, HIERARCHY_NODE_ID)).thenReturn(context);

        AmqpRejectAndDontRequeueException exception = assertThrows(
                AmqpRejectAndDontRequeueException.class,
                () -> deviceImportProcessingService.processImport(message));

        assertInstanceOf(DeviceImportInvalidException.class, exception.getCause());
        assertEquals(DeviceImportStatus.FAILED, deviceImport.getStatus());
        assertNotNull(deviceImport.getStartedAt());
        assertNotNull(deviceImport.getCompletedAt());
        assertEquals(List.of(DeviceImportStatus.PROCESSING, DeviceImportStatus.FAILED), savedStatuses);
        verify(deviceImportContextService).resolveImportContext(
                ORGANIZATION_ID, TEMPLATE_ID, HIERARCHY_NODE_ID);
        verifyNoInteractions(deviceRepository);
    }

    @Test
    void shouldMarkImportFailedAndRethrowContextResolutionFailure() {
        DeviceImportContext context = validContext();
        DeviceImport deviceImport = prepareImport(context);
        List<DeviceImportStatus> savedStatuses = captureSavedStatuses();
        RuntimeException originalException = new IllegalStateException("context lookup failed");
        DeviceImportMessage message = messageWithCsv(deviceImport, VALID_CSV);
        when(deviceImportContextService.resolveImportContext(
                ORGANIZATION_ID, TEMPLATE_ID, HIERARCHY_NODE_ID)).thenThrow(originalException);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> deviceImportProcessingService.processImport(message));

        assertSame(originalException, thrown);
        assertEquals(DeviceImportStatus.FAILED, deviceImport.getStatus());
        assertNotNull(deviceImport.getCompletedAt());
        assertEquals(List.of(DeviceImportStatus.PROCESSING, DeviceImportStatus.FAILED), savedStatuses);
        verify(resultPersistenceService, never()).persist(any(), any());
    }

    @Test
    void shouldRetryFailedImportWhenRabbitRedeliversMessage() {
        DeviceImportContext context = validContext();
        DeviceImport deviceImport = prepareImport(context);
        List<DeviceImportStatus> savedStatuses = captureSavedStatuses();
        RuntimeException originalException = new IllegalStateException("temporary context lookup failure");
        when(deviceImportContextService.resolveImportContext(
                ORGANIZATION_ID, TEMPLATE_ID, HIERARCHY_NODE_ID))
                .thenThrow(originalException)
                .thenReturn(context);
        when(deviceRepository.findByOrganizationAndSerialNumber(context.organization(), "TS1000001"))
                .thenReturn(Optional.empty());
        org.mockito.Mockito.doAnswer(invocation -> {
            DeviceImportProcessingResult result = invocation.getArgument(1);
            deviceImport.setProcessingResults(
                    result.totalRows(), result.createdRows(), result.updatedRows(),
                    result.skippedRows(), result.failedRows());
            deviceImport.markCompleted(Instant.now());
            deviceImportRepository.save(deviceImport);
            return null;
        }).when(resultPersistenceService).persist(any(), any());
        DeviceImportMessage message = messageWithCsv(deviceImport, VALID_CSV);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> deviceImportProcessingService.processImport(message));
        assertSame(originalException, thrown);
        assertEquals(DeviceImportStatus.FAILED, deviceImport.getStatus());

        DeviceImportProcessingResult result = deviceImportProcessingService.processImport(message);

        assertEquals(1, result.createdRows());
        assertEquals(DeviceImportStatus.COMPLETED, deviceImport.getStatus());
        assertNotNull(deviceImport.getCompletedAt());
        assertEquals(List.of(
                DeviceImportStatus.PROCESSING,
                DeviceImportStatus.FAILED,
                DeviceImportStatus.PROCESSING,
                DeviceImportStatus.COMPLETED), savedStatuses);
        verify(deviceRepository).save(any());
    }

    @Test
    void shouldMarkImportFailedAndRethrowResultPersistenceFailure() {
        DeviceImportContext context = validContext();
        DeviceImport deviceImport = prepareImport(context);
        when(deviceImportContextService.resolveImportContext(
                ORGANIZATION_ID, TEMPLATE_ID, HIERARCHY_NODE_ID)).thenReturn(context);
        List<DeviceImportStatus> savedStatuses = captureSavedStatuses();
        RuntimeException originalException = new IllegalStateException("result persistence failed");
        org.mockito.Mockito.doThrow(originalException)
                .when(resultPersistenceService).persist(any(), any());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> deviceImportProcessingService.processImport(messageWithCsv(
                        deviceImport, VALID_CSV)));

        assertSame(originalException, thrown);
        assertEquals(DeviceImportStatus.FAILED, deviceImport.getStatus());
        assertNotNull(deviceImport.getCompletedAt());
        assertEquals(List.of(DeviceImportStatus.PROCESSING, DeviceImportStatus.FAILED), savedStatuses);
    }

    @Test
    void shouldRejectMoreThanTenThousandRowsWithoutProcessingAnyRows() {
        DeviceImportContext context = validContext();
        DeviceImport deviceImport = prepareImport(context);
        when(deviceImportContextService.resolveImportContext(
                ORGANIZATION_ID, TEMPLATE_ID, HIERARCHY_NODE_ID)).thenReturn(context);
        List<DeviceImportStatus> savedStatuses = captureSavedStatuses();

        AmqpRejectAndDontRequeueException exception = assertThrows(
                AmqpRejectAndDontRequeueException.class,
                () -> deviceImportProcessingService.processImport(messageWithRows(
                        deviceImport,
                        DeviceImportConstants.MAX_ROW_COUNT + 1)));

        assertInstanceOf(DeviceImportInvalidException.class, exception.getCause());
        assertEquals(DeviceImportStatus.FAILED, deviceImport.getStatus());
        assertEquals(List.of(DeviceImportStatus.PROCESSING, DeviceImportStatus.FAILED), savedStatuses);
        verifyNoInteractions(deviceRepository);
    }

    @Test
    void shouldAcceptExactlyTenThousandRowsByRowCountRule() {
        DeviceImportContext context = validContext();
        DeviceImport deviceImport = prepareImport(context);
        when(deviceImportContextService.resolveImportContext(
                ORGANIZATION_ID, TEMPLATE_ID, HIERARCHY_NODE_ID)).thenReturn(context);
        List<DeviceImportStatus> savedStatuses = captureSavedStatuses();

        DeviceImportProcessingResult result = deviceImportProcessingService.processImport(messageWithRows(
                deviceImport,
                DeviceImportConstants.MAX_ROW_COUNT));

        assertEquals(DeviceImportConstants.MAX_ROW_COUNT, result.totalRows());
        assertEquals(DeviceImportConstants.MAX_ROW_COUNT, result.failedRows());
        assertEquals(DeviceImportStatus.PROCESSING, deviceImport.getStatus());
        assertEquals(List.of(DeviceImportStatus.PROCESSING), savedStatuses);
    }

    @Test
    void shouldIgnoreDuplicateDeliveryForTerminalImport() {
        DeviceImportContext context = new DeviceImportContext(
                mock(Organization.class),
                mock(DeviceTemplate.class),
                mock(HierarchyNode.class));
        DeviceImport deviceImport = prepareImport(context);
        deviceImport.markCompleted(Instant.now());
        DeviceImportMessage message = messageWithRows(deviceImport, 1);

        DeviceImportProcessingResult result = deviceImportProcessingService.processImport(message);

        assertEquals(0, result.totalRows());
        assertEquals(DeviceImportStatus.COMPLETED, deviceImport.getStatus());
        verifyNoInteractions(deviceRepository, deviceImportContextService);
    }

    private DeviceImportContext validContext() {
        Organization organization = mock(Organization.class);
        DeviceTemplate deviceTemplate = mock(DeviceTemplate.class);
        HierarchyNode hierarchyNode = mock(HierarchyNode.class);
        when(organization.getId()).thenReturn(ORGANIZATION_ID);
        when(deviceTemplate.getId()).thenReturn(TEMPLATE_ID);
        when(hierarchyNode.getId()).thenReturn(HIERARCHY_NODE_ID);
        return new DeviceImportContext(organization, deviceTemplate, hierarchyNode);
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
            long rowCount) {
        StringBuilder csv = new StringBuilder("name,manufacturer,model,serialnumber,firmwareversion,status\n");
        for (int row = 0; row < rowCount; row++) {
            csv.append(",,,,,\n");
        }
        return new DeviceImportMessage(deviceImport.getId(), csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private DeviceImportMessage messageWithCsv(
            DeviceImport deviceImport,
            String csv) {
        return new DeviceImportMessage(deviceImport.getId(), csv.getBytes(StandardCharsets.UTF_8));
    }
}
