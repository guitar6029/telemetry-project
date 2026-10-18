package com.joshsoll.telemetry.platform.device.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportResponse;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.device.importer.service.DeviceImportService;
import com.joshsoll.telemetry.platform.device.service.DeviceService;

@ExtendWith(MockitoExtension.class)
class DeviceControllerImportTest {

    @Mock
    private DeviceService deviceService;

    @Mock
    private DeviceImportService deviceImportService;

    @InjectMocks
    private DeviceController deviceController;

    @Test
    void returnsAcceptedWhenImportIsQueued() {
        User user = mock(User.class);
        UUID organizationId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        UUID hierarchyNodeId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file", "devices.csv", "text/csv", "name,manufacturer,model,serialnumber,firmwareversion,status\n"
                        .getBytes());
        DeviceImportResponse response = new DeviceImportResponse(
                UUID.randomUUID(), "Import job accepted", DeviceImportStatus.QUEUED);
        when(deviceImportService.importDevices(
                user, organizationId, templateId, hierarchyNodeId, file, DeviceImportMode.SKIP_EXISTING))
                .thenReturn(response);

        var result = deviceController.importDevices(
                user, organizationId, templateId, hierarchyNodeId, file, DeviceImportMode.SKIP_EXISTING);

        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        assertEquals(response, result.getBody().getData());
        assertEquals(response.importId(), result.getBody().getData().importId());
    }
}
