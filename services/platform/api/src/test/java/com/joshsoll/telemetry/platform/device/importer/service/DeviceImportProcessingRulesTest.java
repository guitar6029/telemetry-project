package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joshsoll.telemetry.platform.device.DeviceStatus;
import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportProcessingResult;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@ExtendWith(MockitoExtension.class)
class DeviceImportProcessingRulesTest {

    private static final UUID ORGANIZATION_ID = UUID.randomUUID();
    private static final UUID TEMPLATE_ID = UUID.randomUUID();
    private static final UUID HIERARCHY_NODE_ID = UUID.randomUUID();

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceImportContextService deviceImportContextService;

    @InjectMocks
    private DeviceImportProcessingService processingService;

    private UserContext context;

    @BeforeEach
    void setUp() {
        Organization organization = mock(Organization.class);
        DeviceTemplate template = mock(DeviceTemplate.class);
        HierarchyNode hierarchyNode = mock(HierarchyNode.class);
        context = new UserContext(
                organization,
                template,
                hierarchyNode,
                new DeviceImportContext(organization, template, hierarchyNode));
    }

    @Test
    void createsNewDeviceWithTrimmedCsvValuesAndImportContext() {
        prepareContext();
        stubContextIds();
        when(deviceRepository.findByOrganizationAndSerialNumber(context.organization(), "ABC-123"))
                .thenReturn(Optional.empty());

        DeviceImportProcessingResult result = process(
                DeviceImportMode.SKIP_EXISTING,
                row("  Acme Device  ", "  Acme  ", " Model X ", " ABC-123 ", " 2.1 ", " online "));

        ArgumentCaptor<Device> savedCaptor = ArgumentCaptor.forClass(Device.class);
        verify(deviceRepository).save(savedCaptor.capture());
        Device saved = savedCaptor.getValue();
        assertEquals("Acme Device", saved.getName());
        assertEquals("Acme", saved.getManufacturer());
        assertEquals("Model X", saved.getModel());
        assertEquals("ABC-123", saved.getSerialNumber());
        assertEquals("2.1", saved.getFirmwareVersion());
        assertEquals(DeviceStatus.ONLINE, saved.getStatus());
        assertEquals(ORGANIZATION_ID, saved.getOrganizationId());
        assertEquals(HIERARCHY_NODE_ID, saved.getHierarchyNodeId());
        assertEquals(TEMPLATE_ID, saved.getDeviceTemplateId());
        assertEquals(1, result.createdRows());
        assertEquals(0, result.failedRows());
    }

    @Test
    void scopesLookupToOrganizationAndAllowsCreationForThatOrganization() {
        prepareContext();
        when(deviceRepository.findByOrganizationAndSerialNumber(context.organization(), "SHARED-SERIAL"))
                .thenReturn(Optional.empty());

        DeviceImportProcessingResult result = process(
                DeviceImportMode.SKIP_EXISTING,
                row("Device", "Acme", "M1", "SHARED-SERIAL", "1", "ONLINE"));

        verify(deviceRepository).findByOrganizationAndSerialNumber(
                eq(context.organization()), eq("SHARED-SERIAL"));
        assertEquals(1, result.createdRows());
    }

    @Test
    void skipsExistingDeviceWithoutModifyingOrSavingIt() {
        prepareContext();
        Device existing = existingDevice(context, "ABC-123");
        when(deviceRepository.findByOrganizationAndSerialNumber(context.organization(), "ABC-123"))
                .thenReturn(Optional.of(existing));

        Instant beforeUpdatedAt = existing.getUpdatedAt();
        DeviceImportProcessingResult result = process(
                DeviceImportMode.SKIP_EXISTING,
                row("Changed", "Changed", "Changed", "ABC-123", "Changed", "RETIRED"));

        verify(deviceRepository, never()).save(any(Device.class));
        assertEquals("Original", existing.getName());
        assertEquals("ABC-123", existing.getSerialNumber());
        assertEquals(beforeUpdatedAt, existing.getUpdatedAt());
        assertEquals(1, result.skippedRows());
    }

    @Test
    void updatesOnlyImportableFieldsOnExistingDevice() {
        prepareContext();
        stubContextIds();
        Device existing = existingDevice(context, "ABC-123");
        Instant originalCreatedAt = existing.getCreatedAt();
        Instant originalUpdatedAt = existing.getUpdatedAt();
        when(deviceRepository.findByOrganizationAndSerialNumber(context.organization(), "ABC-123"))
                .thenReturn(Optional.of(existing));

        DeviceImportProcessingResult result = process(
                DeviceImportMode.UPDATE_EXISTING,
                row(" Updated name ", " Updated manufacturer ", " Updated model ",
                        "ABC-123", " Updated firmware ", " maintenance "));

        verify(deviceRepository).save(existing);
        assertEquals("Updated name", existing.getName());
        assertEquals("Updated manufacturer", existing.getManufacturer());
        assertEquals("Updated model", existing.getModel());
        assertEquals("ABC-123", existing.getSerialNumber());
        assertEquals("Updated firmware", existing.getFirmwareVersion());
        assertEquals(DeviceStatus.MAINTENANCE, existing.getStatus());
        assertEquals(ORGANIZATION_ID, existing.getOrganizationId());
        assertEquals(HIERARCHY_NODE_ID, existing.getHierarchyNodeId());
        assertEquals(TEMPLATE_ID, existing.getDeviceTemplateId());
        assertEquals(originalCreatedAt, existing.getCreatedAt());
        assertTrue(existing.getUpdatedAt().isAfter(originalUpdatedAt));
        assertEquals(1, result.updatedRows());
    }

    @Test
    void rejectsEveryRowInTrimmedDuplicateGroupBeforeWritesAndContinuesWithOtherRows() {
        prepareContext();
        when(deviceRepository.findByOrganizationAndSerialNumber(context.organization(), "UNIQUE-1"))
                .thenReturn(Optional.empty());

        DeviceImportProcessingResult result = process(
                DeviceImportMode.SKIP_EXISTING,
                row("First", "Acme", "M1", " ABC-123 ", "1", "ONLINE"),
                row("Second", "Acme", "M2", "ABC-123", "2", "ONLINE"),
                row("Valid", "Acme", "M3", " UNIQUE-1 ", "3", "ONLINE"));

        assertEquals(3, result.totalRows());
        assertEquals(1, result.createdRows());
        assertEquals(2, result.failedRows());
        assertEquals(2, result.errors().size());
        assertTrue(result.errors().stream().allMatch(error -> error.errors().stream()
                .anyMatch(message -> message.contains("more than once"))));
        verify(deviceRepository, never()).findByOrganizationAndSerialNumber(context.organization(), "ABC-123");

        ArgumentCaptor<Device> savedCaptor = ArgumentCaptor.forClass(Device.class);
        verify(deviceRepository).save(savedCaptor.capture());
        assertEquals("UNIQUE-1", savedCaptor.getValue().getSerialNumber());
    }

    @Test
    void validRowsContinueWhenAnotherRowFailsValidation() {
        prepareContext();
        when(deviceRepository.findByOrganizationAndSerialNumber(context.organization(), "A"))
                .thenReturn(Optional.empty());
        when(deviceRepository.findByOrganizationAndSerialNumber(context.organization(), "C"))
                .thenReturn(Optional.empty());

        DeviceImportProcessingResult result = process(
                DeviceImportMode.SKIP_EXISTING,
                row("First", "Acme", "M1", "A", "1", "ONLINE"),
                row(" ", "Acme", "M2", "B", "2", "ONLINE"),
                row("Third", "Acme", "M3", "C", "3", "ONLINE"));

        assertEquals(3, result.totalRows());
        assertEquals(2, result.createdRows());
        assertEquals(1, result.failedRows());
        assertFalse(result.errors().isEmpty());
        verify(deviceRepository, never()).findByOrganizationAndSerialNumber(context.organization(), "B");
        verify(deviceRepository, org.mockito.Mockito.times(2)).save(any(Device.class));
    }

    @Test
    void propagatesUnexpectedRepositoryRuntimeFailure() {
        prepareContext();
        when(deviceRepository.findByOrganizationAndSerialNumber(context.organization(), "A"))
                .thenThrow(new IllegalStateException("database unavailable"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> process(
                        DeviceImportMode.SKIP_EXISTING,
                        row("Device", "Acme", "M1", "A", "1", "ONLINE")));

        assertEquals("database unavailable", exception.getMessage());
    }

    private void prepareContext() {
        when(deviceImportContextService.resolveImportContext(
                ORGANIZATION_ID, TEMPLATE_ID, HIERARCHY_NODE_ID))
                .thenReturn(context.deviceImportContext());
    }

    private void stubContextIds() {
        when(context.organization().getId()).thenReturn(ORGANIZATION_ID);
        when(context.template().getId()).thenReturn(TEMPLATE_ID);
        when(context.hierarchyNode().getId()).thenReturn(HIERARCHY_NODE_ID);
    }

    private DeviceImportProcessingResult process(DeviceImportMode mode, String... rows) {
        String csv = "name,manufacturer,model,serialnumber,firmwareversion,status\n"
                + String.join("\n", rows)
                + "\n";
        DeviceImportMessage message = new DeviceImportMessage(
                ORGANIZATION_ID,
                TEMPLATE_ID,
                HIERARCHY_NODE_ID,
                mode,
                csv.getBytes(StandardCharsets.UTF_8));
        return processingService.processImport(message);
    }

    private String row(String name, String manufacturer, String model, String serial, String firmware, String status) {
        return String.join(",", name, manufacturer, model, serial, firmware, status);
    }

    private Device existingDevice(UserContext context, String serialNumber) {
        Instant now = Instant.now().minusSeconds(30);
        return new Device(
                "Original",
                "Original manufacturer",
                "Original model",
                serialNumber,
                "Original firmware",
                DeviceStatus.OFFLINE,
                context.organization(),
                context.hierarchyNode(),
                context.template(),
                now,
                now);
    }

    private record UserContext(
            Organization organization,
            DeviceTemplate template,
            HierarchyNode hierarchyNode,
            DeviceImportContext deviceImportContext) {
    }
}
