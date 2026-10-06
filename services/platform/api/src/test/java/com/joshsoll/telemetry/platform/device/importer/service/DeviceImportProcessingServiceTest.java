package com.joshsoll.telemetry.platform.device.importer.service;

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
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
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
}
