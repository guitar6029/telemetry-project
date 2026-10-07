package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
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
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
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
        DeviceImportMessage message = new DeviceImportMessage(
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

        AmqpRejectAndDontRequeueException exception = assertThrows(
                AmqpRejectAndDontRequeueException.class,
                () -> deviceImportProcessingService.processImport(messageWithRows(
                        organizationId,
                        templateId,
                        hierarchyNodeId,
                        DeviceImportConstants.MAX_ROW_COUNT + 1)));

        assertInstanceOf(DeviceImportInvalidException.class, exception.getCause());
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

        DeviceImportProcessingResult result = deviceImportProcessingService.processImport(messageWithRows(
                organizationId,
                templateId,
                hierarchyNodeId,
                DeviceImportConstants.MAX_ROW_COUNT));

        assertEquals(DeviceImportConstants.MAX_ROW_COUNT, result.totalRows());
        assertEquals(DeviceImportConstants.MAX_ROW_COUNT, result.failedRows());
    }

    private DeviceImportContext validContext() {
        return new DeviceImportContext(
                mock(Organization.class),
                mock(DeviceTemplate.class),
                mock(HierarchyNode.class));
    }

    private DeviceImportMessage messageWithRows(
            UUID organizationId,
            UUID templateId,
            UUID hierarchyNodeId,
            long rowCount) {
        StringBuilder csv = new StringBuilder("name,manufacturer,model,serialnumber,firmwareversion,status\n");
        for (int row = 0; row < rowCount; row++) {
            csv.append(",,,,,\n");
        }
        return new DeviceImportMessage(
                organizationId,
                templateId,
                hierarchyNodeId,
                DeviceImportMode.SKIP_EXISTING,
                csv.toString().getBytes(StandardCharsets.UTF_8));
    }
}
