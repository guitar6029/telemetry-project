package com.joshsoll.telemetry.platform.device.importer.service;

import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.device.DeviceStatus;
import com.joshsoll.telemetry.platform.device.constants.DeviceConstants;
import com.joshsoll.telemetry.platform.device.dto.CreateDeviceRequest;
import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportError;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportPreview;
import com.joshsoll.telemetry.platform.device.importer.dto.PreparedDeviceImportRow;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportRepository;
import com.joshsoll.telemetry.platform.device.importer.storage.DeviceImportArtifactStorage;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateNotFoundException;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateOrganizationMismatchException;
import com.joshsoll.telemetry.platform.devicetemplate.repository.DeviceTemplateRepository;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.hierarchy.exception.HierarchyNodeNotFoundException;
import com.joshsoll.telemetry.platform.hierarchy.exception.HierarchyNodeOrganizationMismatchException;
import com.joshsoll.telemetry.platform.hierarchy.repository.HierarchyNodeRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Service
public class DeviceImportService {

    private final DeviceRepository deviceRepository;
    private final AuthorizationService authorizationService;
    private final DeviceTemplateRepository deviceTemplateRepository;
    private final HierarchyNodeRepository hierarchyNodeRepository;
    private final DeviceImportRepository deviceImportRepository;
    private final DeviceImportArtifactSerializer deviceImportArtifactSerializer;
    private final DeviceImportArtifactStorage deviceImportArtifactStorage;
    private static final Set<String> REQUIRED_HEADERS = Set.of(
            "name",
            "manufacturer",
            "model",
            "serialNumber",
            "firmwareVersion",
            "status");

    public record DeviceImportParseResult(
            List<CreateDeviceRequest> validRows,
            List<DeviceImportError> errors) {
    }

    public DeviceImportService(
            AuthorizationService authorizationService,
            DeviceTemplateRepository deviceTemplateRepository,
            HierarchyNodeRepository hierarchyNodeRepository,
            DeviceRepository deviceRepository,
            DeviceImportRepository deviceImportRepository,
            DeviceImportArtifactSerializer deviceImportArtifactSerializer,
            DeviceImportArtifactStorage deviceImportArtifactStorage) {
        this.authorizationService = authorizationService;
        this.deviceTemplateRepository = deviceTemplateRepository;
        this.hierarchyNodeRepository = hierarchyNodeRepository;
        this.deviceRepository = deviceRepository;
        this.deviceImportRepository = deviceImportRepository;
        this.deviceImportArtifactSerializer = deviceImportArtifactSerializer;
        this.deviceImportArtifactStorage = deviceImportArtifactStorage;
    }

    public DeviceImportPreview setupPreviewImport(
            User authenticatedUser,
            UUID organizationId,
            UUID templateId,
            UUID hierarchyNodeId,
            MultipartFile file) {

        DeviceImportContext deviceContext = validateImportContext(
                authenticatedUser,
                organizationId,
                templateId,
                hierarchyNodeId,
                file);

        DeviceImportParseResult parsedResults = parseCSVFile(file, deviceContext);

        List<PreparedDeviceImportRow> preparedRows = parsedResults.validRows()
                .stream()
                .map(row -> new PreparedDeviceImportRow(
                        row.getName(),
                        row.getManufacturer(),
                        row.getModel(),
                        row.getSerialNumber(),
                        row.getFirmwareVersion(),
                        row.getStatus()))
                .toList();

        InputStream artifact = deviceImportArtifactSerializer.serialize(preparedRows);

        UUID id = UUID.randomUUID();

        String storageKey = deviceImportArtifactStorage.store(id, artifact);

        DeviceImport deviceImport = new DeviceImport(
                DeviceImportMode.SKIP_EXISTING,
                deviceContext.organization(),
                deviceContext.deviceTemplate(),
                deviceContext.hierarchyNode(),
                storageKey,
                parsedResults.validRows().size() + parsedResults.errors().size(),
                parsedResults.validRows().size(),
                parsedResults.errors().size());

        DeviceImport savedImport = deviceImportRepository.save(deviceImport);

        return toResponsePreview(savedImport, parsedResults);
    }

    private DeviceImportPreview toResponsePreview(
            DeviceImport savedImport,
            DeviceImportParseResult parsedResults) {
        return new DeviceImportPreview(
                savedImport.getId(),
                savedImport.getTotalRows(),
                savedImport.getValidRows(),
                savedImport.getInvalidRows(),
                parsedResults.validRows().stream()
                        .limit(10)
                        .toList(),
                parsedResults.errors());
    }

    public DeviceImportContext validateImportContext(
            User authenticatedUser,
            UUID organizationId,
            UUID templateId,
            UUID hierarchyNodeId,
            MultipartFile file) {

        Organization organization = authorizationService.requireOrganizationAccess(authenticatedUser, organizationId);

        if (file == null || file.isEmpty()) {
            throw new DeviceImportInvalidException("Import file is required.");
        }

        String contentType = file.getContentType();

        if (!"text/csv".equalsIgnoreCase(contentType)) {
            throw new DeviceImportInvalidException("Import file must be a CSV.");
        }

        DeviceTemplate deviceTemplate = deviceTemplateRepository.findById(templateId)
                .orElseThrow(() -> new DeviceTemplateNotFoundException(templateId));

        HierarchyNode hierarchyNode = hierarchyNodeRepository.findById(hierarchyNodeId)
                .orElseThrow(() -> new HierarchyNodeNotFoundException(hierarchyNodeId));

        if (!deviceTemplate.getOrganizationId().equals(organization.getId())) {
            throw new DeviceTemplateOrganizationMismatchException();
        }

        if (!hierarchyNode.getOrganization().getId().equals(organization.getId())) {
            throw new HierarchyNodeOrganizationMismatchException();
        }

        return new DeviceImportContext(
                organization,
                deviceTemplate,
                hierarchyNode);
    }

    public void importDevices(
            User authenticatedUser,
            UUID organizationId,
            UUID templateId,
            UUID hierarchyNodeId,
            MultipartFile file) {

        Organization organization = authorizationService.requireOrganizationAccess(authenticatedUser, organizationId);

        if (file == null || file.isEmpty()) {
            throw new DeviceImportInvalidException("Import file is required.");
        }

        String contentType = file.getContentType();

        if (!"text/csv".equalsIgnoreCase(contentType)) {
            throw new DeviceImportInvalidException("Import file must be a CSV.");
        }

        DeviceTemplate deviceTemplate = deviceTemplateRepository.findById(templateId)
                .orElseThrow(() -> new DeviceTemplateNotFoundException(templateId));

        HierarchyNode hierarchyNode = hierarchyNodeRepository.findById(hierarchyNodeId)
                .orElseThrow(() -> new HierarchyNodeNotFoundException(hierarchyNodeId));

        if (!deviceTemplate.getOrganizationId().equals(organization.getId())) {
            throw new DeviceTemplateOrganizationMismatchException();
        }

        if (!hierarchyNode.getOrganization().getId().equals(organization.getId())) {
            throw new HierarchyNodeOrganizationMismatchException();
        }
        // parse , validate , normalize, format - helpers
        // first validate all headers - all or nothing

        DeviceImportContext deviceContext = new DeviceImportContext(
                organization,
                deviceTemplate,
                hierarchyNode);

        parseCSVFile(file, deviceContext);

    }

    private DeviceImportParseResult parseCSVFile(MultipartFile file, DeviceImportContext deviceContext) {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();
        try (
                Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVParser parser = format.parse(reader);

        ) {

            // verify headers
            verifyHeaders(parser);
            // parse rows , we get valid devices and error objects back
            DeviceImportParseResult parsedResults = parseRows(parser, deviceContext);
            // // save the devices
            // saveDevices(parsedResults.validRows(), deviceContext);
            return parsedResults;
        } catch (IOException ex) {
            throw new DeviceImportInvalidException("Unable to read import file");
        }

    }

    private void verifyHeaders(CSVParser parser) {
        Set<String> headers = parser.getHeaderMap()
                .keySet()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (!headers.equals(REQUIRED_HEADERS)) {
            throw new DeviceImportInvalidException("Invalid CSV headers.");
        }
    }

    private DeviceImportParseResult parseRows(CSVParser parser, DeviceImportContext deviceContext) {

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
            validateName(name).ifPresent((rowErrors::add));
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
                    DeviceStatus.valueOf(status.trim().toUpperCase()),
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

        if (name.length() < DeviceConstants.NAME_MIN_LENGTH || name.length() > DeviceConstants.NAME_MAX_LENGTH) {
            return Optional.of("Name must be between " + DeviceConstants.NAME_MIN_LENGTH + " and "
                    + DeviceConstants.NAME_MAX_LENGTH + " characters.");
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

    private Optional<String> validateFirmwareVersion(String firmwareVersion) {
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
            return Optional.empty(); // Valid status
        } catch (IllegalArgumentException e) {
            return Optional.of("Invalid status: '" + status + "'. Must be one of: "
                    + java.util.Arrays.toString(DeviceStatus.values()));
        }
    }

    private Device toEntity(CreateDeviceRequest deviceRequest, DeviceImportContext deviceImportContext) {

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
                .map(request -> toEntity(request, deviceImportContext))
                .toList();

        deviceRepository.saveAll(entities);
    }

}
