package com.joshsoll.telemetry.platform.device.importer.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.device.DeviceStatus;
import com.joshsoll.telemetry.platform.device.constants.DeviceConstants;
import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.device.exception.DuplicateDeviceSerialNumberException;
import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportError;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportParseResult;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportProcessingResult;
import com.joshsoll.telemetry.platform.device.importer.dto.PreparedDeviceImportRow;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;

@Service
public class DeviceImportProcessingService {

    private final DeviceRepository deviceRepository;
    private final DeviceImportContextService deviceImportContextService;

    public DeviceImportProcessingService(
            DeviceRepository deviceRepository,
            DeviceImportContextService deviceImportContextService) {
        this.deviceRepository = deviceRepository;
        this.deviceImportContextService = deviceImportContextService;
    }

    public DeviceImportProcessingResult processImport(DeviceImportMessage message) {
        DeviceImportContext context = deviceImportContextService.resolveImportContext(
                message.organizationId(),
                message.templateId(),
                message.hierarchyNodeId());

        InputStream inputStream = new ByteArrayInputStream(message.csvData());

        DeviceImportParseResult parsedResults;
        try {
            parsedResults = parseCSVFile(inputStream, context);
        } catch (DeviceImportInvalidException exception) {
            throw new AmqpRejectAndDontRequeueException(
                    "Device import contains an invalid CSV",
                    exception);
        }

        return processRows(parsedResults, context, message.importMode());
    }

    private DeviceImportParseResult parseCSVFile(
            InputStream inputStream,
            DeviceImportContext deviceContext) {

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();

        try (
                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                CSVParser parser = format.parse(reader)) {

            verifyHeaders(parser);
            return parseRows(parser);
        } catch (IOException exception) {
            throw new DeviceImportInvalidException("Unable to read import file");
        }
    }

    private void verifyHeaders(CSVParser parser) {
        Set<String> headers = parser.getHeaderMap()
                .keySet()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (!headers.equals(DeviceImportConstants.REQUIRED_HEADERS)) {
            throw new DeviceImportInvalidException("Invalid CSV headers.");
        }
    }

    private DeviceImportParseResult parseRows(CSVParser parser) {

        List<PreparedDeviceImportRow> rows = new ArrayList<>();
        List<List<String>> rowErrors = new ArrayList<>();
        Map<String, List<Integer>> rowsBySerialNumber = new HashMap<>();

        for (CSVRecord record : parser) {
            if (rows.size() >= DeviceImportConstants.MAX_ROW_COUNT) {
                throw new DeviceImportInvalidException(
                        "Import file must not contain more than 10,000 rows.");
            }

            PreparedDeviceImportRow row = new PreparedDeviceImportRow(
                    record.getRecordNumber(),
                    normalize(record.get("name")),
                    normalize(record.get("manufacturer")),
                    normalize(record.get("model")),
                    normalize(record.get("serialnumber")),
                    normalize(record.get("firmwareversion")),
                    normalize(record.get("status")));
            rows.add(row);

            List<String> errors = validateRow(row);
            rowErrors.add(errors);

            if (hasSerialNumber(row.serialNumber())) {
                rowsBySerialNumber.computeIfAbsent(row.serialNumber(), ignored -> new ArrayList<>())
                        .add(rows.size() - 1);
            }
        }

        List<PreparedDeviceImportRow> validRows = new ArrayList<>();
        List<DeviceImportError> errors = new ArrayList<>();

        for (int index = 0; index < rows.size(); index++) {
            PreparedDeviceImportRow row = rows.get(index);
            List<String> currentErrors = rowErrors.get(index);
            List<Integer> duplicateRows = rowsBySerialNumber.get(row.serialNumber());

            if (hasSerialNumber(row.serialNumber()) && duplicateRows.size() > 1) {
                currentErrors.add("Serial number appears more than once in this import.");
            }

            if (!currentErrors.isEmpty()) {
                errors.add(new DeviceImportError(row.rowNumber(), List.copyOf(currentErrors)));
            } else {
                validRows.add(row);
            }
        }

        return new DeviceImportParseResult(validRows, errors);
    }

    private List<String> validateRow(PreparedDeviceImportRow row) {
        List<String> errors = new ArrayList<>();

        validateName(row.name()).ifPresent(errors::add);
        validateRequired(row.model(), "Model").ifPresent(errors::add);
        validateRequired(row.serialNumber(), "Serial Number").ifPresent(errors::add);
        validateRequired(row.manufacturer(), "Manufacturer").ifPresent(errors::add);
        validateRequired(row.firmwareVersion(), "Firmware Version").ifPresent(errors::add);
        validateStatus(row.status()).ifPresent(errors::add);

        return errors;
    }

    private Optional<String> validateName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.of("Name is required");
        }

        if (name.length() < DeviceConstants.NAME_MIN_LENGTH
                || name.length() > DeviceConstants.NAME_MAX_LENGTH) {
            return Optional.of(
                    "Name must be between "
                            + DeviceConstants.NAME_MIN_LENGTH
                            + " and "
                            + DeviceConstants.NAME_MAX_LENGTH
                            + " characters.");
        }

        return Optional.empty();
    }

    private Optional<String> validateRequired(String value, String label) {
        if (value == null || value.isBlank()) {
            return Optional.of(label + " is required");
        }
        return Optional.empty();
    }

    private Optional<String> validateStatus(String status) {
        if (status == null || status.isBlank()) {
            return Optional.of("Status is required");
        }
        try {
            DeviceStatus.valueOf(status.toUpperCase());
            return Optional.empty();
        } catch (IllegalArgumentException exception) {
            return Optional.of(
                    "Invalid status: '"
                            + status
                            + "'. Must be one of: "
                            + java.util.Arrays.toString(DeviceStatus.values()));
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private DeviceImportProcessingResult processRows(
            DeviceImportParseResult parsedResults,
            DeviceImportContext context,
            DeviceImportMode importMode) {

        long createdRows = 0;
        long updatedRows = 0;
        long skippedRows = 0;
        List<DeviceImportError> errors = new ArrayList<>(parsedResults.errors());

        for (PreparedDeviceImportRow row : parsedResults.validRows()) {
            try {
                Optional<Device> existingDevice = deviceRepository.findByOrganizationAndSerialNumber(
                        context.organization(),
                        row.serialNumber());

                if (existingDevice.isEmpty()) {
                    deviceRepository.save(toEntity(row, context));
                    createdRows++;
                    continue;
                }

                if (importMode == DeviceImportMode.SKIP_EXISTING) {
                    skippedRows++;
                    continue;
                }

                if (importMode == DeviceImportMode.UPDATE_EXISTING) {
                    Device device = existingDevice.get();
                    device.updateImportableFields(
                            row.name(),
                            row.manufacturer(),
                            row.model(),
                            row.firmwareVersion(),
                            DeviceStatus.valueOf(row.status().toUpperCase()));
                    deviceRepository.save(device);
                    updatedRows++;
                    continue;
                }

                errors.add(new DeviceImportError(
                        row.rowNumber(),
                        List.of("Import mode is required.")));
            } catch (DuplicateDeviceSerialNumberException exception) {
                errors.add(new DeviceImportError(
                        row.rowNumber(),
                        List.of(exception.getMessage())));
            }
        }

        long totalRows = parsedResults.validRows().size() + parsedResults.errors().size();
        return new DeviceImportProcessingResult(
                totalRows,
                createdRows,
                updatedRows,
                skippedRows,
                errors.size(),
                List.copyOf(errors));
    }

    private Device toEntity(PreparedDeviceImportRow row, DeviceImportContext context) {
        Instant now = Instant.now();
        return new Device(
                row.name(),
                row.manufacturer(),
                row.model(),
                row.serialNumber(),
                row.firmwareVersion(),
                DeviceStatus.valueOf(row.status().toUpperCase()),
                context.organization(),
                context.hierarchyNode(),
                context.deviceTemplate(),
                now,
                now);
    }

    private boolean hasSerialNumber(String serialNumber) {
        return serialNumber != null && !serialNumber.isBlank();
    }
}
