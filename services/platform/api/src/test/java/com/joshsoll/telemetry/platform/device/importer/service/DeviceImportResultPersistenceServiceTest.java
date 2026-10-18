package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportError;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportProcessingResult;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportErrorRepository;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportRepository;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@ExtendWith(MockitoExtension.class)
class DeviceImportResultPersistenceServiceTest {
    @Mock private DeviceImportRepository imports;
    @Mock private DeviceImportErrorRepository errors;

    @Test
    void persistsSummaryAndOneErrorRecordForFailedRowWithSeveralMessages() {
        DeviceImport deviceImport = newImport();
        when(imports.findById(deviceImport.getId())).thenReturn(Optional.of(deviceImport));
        DeviceImportResultPersistenceService service = new DeviceImportResultPersistenceService(imports, errors);
        DeviceImportProcessingResult result = new DeviceImportProcessingResult(
                3, 1, 0, 1, 1, List.of(new DeviceImportError(3, List.of("bad name", "missing model"))));

        service.persist(deviceImport.getId(), result);

        assertEquals(3, deviceImport.getTotalRows());
        assertEquals(1, deviceImport.getCreatedRows());
        assertEquals(1, deviceImport.getSkippedRows());
        assertEquals(1, deviceImport.getFailedRows());
        assertEquals(DeviceImportStatus.COMPLETED_WITH_ERRORS, deviceImport.getStatus());
        verify(errors).deleteByDeviceImport_Id(deviceImport.getId());
        verify(errors).saveAll(anyList());
        verify(imports).save(deviceImport);
    }

    @Test
    void marksSuccessfulImportCompletedAndPersistsAllCounts() {
        DeviceImport deviceImport = newImport();
        when(imports.findById(deviceImport.getId())).thenReturn(Optional.of(deviceImport));
        DeviceImportResultPersistenceService service = new DeviceImportResultPersistenceService(imports, errors);

        service.persist(deviceImport.getId(), new DeviceImportProcessingResult(2, 1, 1, 0, 0, List.of()));

        assertEquals(DeviceImportStatus.COMPLETED, deviceImport.getStatus());
        assertEquals(2, deviceImport.getTotalRows());
        assertEquals(1, deviceImport.getUpdatedRows());
        assertEquals(0, deviceImport.getFailedRows());
    }

    private DeviceImport newImport() {
        return new DeviceImport(org.mockito.Mockito.mock(Organization.class),
                org.mockito.Mockito.mock(DeviceTemplate.class), org.mockito.Mockito.mock(HierarchyNode.class),
                "devices.csv", DeviceImportMode.SKIP_EXISTING, Instant.now());
    }
}
