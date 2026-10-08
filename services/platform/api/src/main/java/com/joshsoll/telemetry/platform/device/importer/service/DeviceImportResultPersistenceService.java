package com.joshsoll.telemetry.platform.device.importer.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportProcessingResult;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.entity.PersistedDeviceImportError;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportErrorRepository;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportRepository;

@Service
public class DeviceImportResultPersistenceService {
    private final DeviceImportRepository imports;
    private final DeviceImportErrorRepository errors;

    public DeviceImportResultPersistenceService(DeviceImportRepository imports, DeviceImportErrorRepository errors) {
        this.imports = imports;
        this.errors = errors;
    }

    @Transactional
    public void persist(UUID importId, DeviceImportProcessingResult result) {
        DeviceImport deviceImport = imports.findById(importId)
                .orElseThrow(() -> new IllegalStateException("Device import not found: " + importId));
        deviceImport.setProcessingResults(result.totalRows(), result.createdRows(), result.updatedRows(),
                result.skippedRows(), result.failedRows());
        if (result.failedRows() > 0) deviceImport.markCompletedWithErrors(Instant.now());
        else deviceImport.markCompleted(Instant.now());
        errors.deleteByDeviceImport_Id(importId);
        List<PersistedDeviceImportError> rows = result.errors().stream()
                .map(error -> new PersistedDeviceImportError(deviceImport, error)).toList();
        errors.saveAll(rows);
        imports.save(deviceImport);
    }
}
