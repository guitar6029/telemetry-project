package com.joshsoll.telemetry.platform.device.importer.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.nio.charset.StandardCharsets;

import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.device.DeviceStatus;
import com.joshsoll.telemetry.platform.device.constants.DeviceConstants;
import com.joshsoll.telemetry.platform.device.dto.CreateDeviceRequest;
import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportError;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportParseResult;
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

    public void processImport(DeviceImportMessage message) {
        DeviceImportContext context = deviceImportContextService.resolveImportContext(
                message.organizationId(),
                message.templateId(),
                message.hierarchyNodeId());

        InputStream inputStream = new ByteArrayInputStream(message.csvData());

        DeviceImportParseResult parsedResults = parseCSVFile(inputStream, context);

        saveDevices(parsedResults.validRows(), context);

    }

    private DeviceImportParseResult parseCSVFile(
            InputStream inputStream,
            DeviceImportContext deviceContext) {

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();

        try (
                Reader reader = new InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8);
                CSVParser parser = format.parse(reader);) {

            // Verify headers
            verifyHeaders(parser);

            // Parse rows; we get valid devices and error objects back
            DeviceImportParseResult parsedResults = parseRows(parser, deviceContext);

            return parsedResults;

        } catch (IOException ex) {
            throw new DeviceImportInvalidException(
                    "Unable to read import file");
        }
    }

    private void verifyHeaders(CSVParser parser) {

        Set<String> headers = parser.getHeaderMap()
                .keySet()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (!headers.equals(DeviceImportConstants.REQUIRED_HEADERS)) {
            throw new DeviceImportInvalidException(
                    "Invalid CSV headers.");
        }
    }

    private DeviceImportParseResult parseRows(
            CSVParser parser,
            DeviceImportContext deviceContext) {

        List<CreateDeviceRequest> validRows = new ArrayList<>();
        List<DeviceImportError> errors = new ArrayList<>();

        for (CSVRecord record : parser) {

            String name = record.get("name");
            String manufacturer = record.get("manufacturer");
            String model = record.get("model");
            String serialNumber = record.get("serialNumber");
            String firmwareVersion = record.get("firmwareVersion");
            String status = record.get("status");

            List<String> rowErrors = new ArrayList<>();

            validateName(name).ifPresent(rowErrors::add);
            validateModel(model).ifPresent(rowErrors::add);
            validateSerialNumber(serialNumber).ifPresent(rowErrors::add);
            validateManufacturer(manufacturer).ifPresent(rowErrors::add);
            validateFirmwareVersion(firmwareVersion).ifPresent(rowErrors::add);
            validateStatus(status).ifPresent(rowErrors::add);

            if (!rowErrors.isEmpty()) {
                errors.add(new DeviceImportError(
                        record.getRecordNumber(),
                        rowErrors));

                continue;
            }

            validRows.add(new CreateDeviceRequest(
                    name,
                    model,
                    serialNumber,
                    manufacturer,
                    firmwareVersion,
                    DeviceStatus.valueOf(
                            status.trim().toUpperCase()),
                    deviceContext.organization().getId(),
                    deviceContext.hierarchyNode().getId(),
                    deviceContext.deviceTemplate().getId()));
        }

        return new DeviceImportParseResult(validRows, errors);
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

    private Optional<String> validateModel(String model) {

        if (model == null || model.isBlank()) {
            return Optional.of("Model is required");
        }

        return Optional.empty();
    }

    private Optional<String> validateSerialNumber(String serialNumber) {

        if (serialNumber == null || serialNumber.isBlank()) {
            return Optional.of("Serial Number is required");
        }

        return Optional.empty();
    }

    private Optional<String> validateManufacturer(String manufacturer) {

        if (manufacturer == null || manufacturer.isBlank()) {
            return Optional.of("Manufacturer is required");
        }

        return Optional.empty();
    }

    private Optional<String> validateFirmwareVersion(
            String firmwareVersion) {

        if (firmwareVersion == null || firmwareVersion.isBlank()) {
            return Optional.of("Firmware Version is required");
        }

        return Optional.empty();
    }

    private Optional<String> validateStatus(String status) {

        if (status == null || status.isBlank()) {
            return Optional.of("Status is required");
        }

        try {
            DeviceStatus.valueOf(status.trim().toUpperCase());

            return Optional.empty();

        } catch (IllegalArgumentException e) {
            return Optional.of(
                    "Invalid status: '"
                            + status
                            + "'. Must be one of: "
                            + java.util.Arrays.toString(DeviceStatus.values()));
        }
    }

    private Device toEntity(
            CreateDeviceRequest deviceRequest,
            DeviceImportContext deviceImportContext) {

        Instant now = Instant.now();

        return new Device(
                deviceRequest.getName(),
                deviceRequest.getManufacturer(),
                deviceRequest.getModel(),
                deviceRequest.getSerialNumber(),
                deviceRequest.getFirmwareVersion(),
                deviceRequest.getStatus(),
                deviceImportContext.organization(),
                deviceImportContext.hierarchyNode(),
                deviceImportContext.deviceTemplate(),
                now,
                now);
    }

    private void saveDevices(
            List<CreateDeviceRequest> deviceRequests,
            DeviceImportContext deviceImportContext) {

        List<Device> entities = deviceRequests.stream()
                .map(request -> toEntity(
                        request,
                        deviceImportContext))
                .toList();

        deviceRepository.saveAll(entities);
    }
}
