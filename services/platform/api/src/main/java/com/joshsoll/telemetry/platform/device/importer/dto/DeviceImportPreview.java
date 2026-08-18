package com.joshsoll.telemetry.platform.device.importer.dto;

import java.util.List;
import java.util.UUID;

import com.joshsoll.telemetry.platform.device.dto.CreateDeviceRequest;

public record DeviceImportPreview(
                UUID id,
                int totalRows,
                int validRows,
                int invalidRows,
                List<CreateDeviceRequest> previewRows,
                List<DeviceImportError> errors) {

}
