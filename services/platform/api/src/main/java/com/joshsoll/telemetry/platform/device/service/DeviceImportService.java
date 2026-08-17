package com.joshsoll.telemetry.platform.device.service;

import java.io.Reader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
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

    private final AuthorizationService authorizationService;
    private final DeviceTemplateRepository deviceTemplateRepository;
    private final HierarchyNodeRepository hierarchyNodeRepository;
    private static final Set<String> REQUIRED_HEADERS = Set.of(
            "name",
            "manufacturer",
            "model",
            "serialNumber",
            "firmwareVersion",
            "status");

    public DeviceImportService(
            AuthorizationService authorizationService,
            DeviceTemplateRepository deviceTemplateRepository,
            HierarchyNodeRepository hierarchyNodeRepository) {
        this.authorizationService = authorizationService;
        this.deviceTemplateRepository = deviceTemplateRepository;
        this.hierarchyNodeRepository = hierarchyNodeRepository;
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
        parseCSVFile(file);

    }

    private void parseCSVFile(MultipartFile file) {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();
        try (
                Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVParser parser = format.parse(reader);

        ) {

            verifyHeaders(parser);
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

}
