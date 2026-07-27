package com.joshsoll.telemetry.platform.device.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.device.dto.CreateDeviceRequest;
import com.joshsoll.telemetry.platform.device.dto.DeviceResponse;
import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.device.exception.DeviceNotFoundException;
import com.joshsoll.telemetry.platform.device.exception.DuplicateDeviceSerialNumberException;
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateNotFoundException;
import com.joshsoll.telemetry.platform.devicetemplate.repository.DeviceTemplateRepository;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.hierarchy.exception.HierarchyNodeNotFoundException;
import com.joshsoll.telemetry.platform.hierarchy.exception.HierarchyNodeOrganizationMismatchException;
import com.joshsoll.telemetry.platform.hierarchy.repository.HierarchyNodeRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.exception.OrganizationNotFoundException;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final OrganizationRepository organizationRepository;
    private final HierarchyNodeRepository hierarchyNodeRepository;
    private final DeviceTemplateRepository deviceTemplateRepository;

    public DeviceService(
            DeviceRepository deviceRepository,
            OrganizationRepository organizationRepository,
            HierarchyNodeRepository hierarchyNodeRepository,
            DeviceTemplateRepository deviceTemplateRepository) {
        this.deviceRepository = deviceRepository;
        this.organizationRepository = organizationRepository;
        this.hierarchyNodeRepository = hierarchyNodeRepository;
        this.deviceTemplateRepository = deviceTemplateRepository;
    }

    // create
    public DeviceResponse createDevice(CreateDeviceRequest request) {

        Instant now = Instant.now();

        // find organization
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new OrganizationNotFoundException(request.getOrganizationId()));

        // find hierarchy node
        HierarchyNode hierarchyNode = hierarchyNodeRepository.findById(request.getHierarchyNodeId())
                .orElseThrow(() -> new HierarchyNodeNotFoundException(request.getHierarchyNodeId()));

        // Find device template
        DeviceTemplate deviceTemplate = deviceTemplateRepository.findById(request.getDeviceTemplateId())
                .orElseThrow(() -> new DeviceTemplateNotFoundException(request.getDeviceTemplateId()));

        // Validate hierarchy belongs to organization
        if (!hierarchyNode.getOrganization().getId().equals(organization.getId())) {
            throw new HierarchyNodeOrganizationMismatchException();
        }

        // Validate device template
        if (!deviceTemplate.getOrganizationId().equals(organization.getId())) {
            throw new IllegalArgumentException("Device template does not belong to the organization");
        }

        // Validate serial number uniqueness
        if (deviceRepository.existsByOrganizationAndSerialNumber(organization,
                request.getSerialNumber())) {
            throw new DuplicateDeviceSerialNumberException(
                    request.getSerialNumber());
        }

        Device device = new Device(
                request.getName(),
                request.getManufacturer(),
                request.getModel(),
                request.getSerialNumber(),
                request.getFirmwareVersion(),
                request.getStatus(),
                organization,
                hierarchyNode,
                deviceTemplate,
                now,
                now);

        Device savedDevice = deviceRepository.save(device);

        return toResponse(savedDevice);
    }

    // get device by id
    public DeviceResponse getDeviceById(UUID id) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        return toResponse(device);
    }

    // list devices
    public PagedApiResponse<DeviceResponse> getDevices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Device> devices = deviceRepository.findAll(pageable);

        List<DeviceResponse> responses = new ArrayList<>();

        for (Device device : devices) {
            responses.add(toResponse(device));
        }

        return new PagedApiResponse<>(
                responses,
                "",
                page,
                size,
                devices.getTotalElements(),
                devices.getTotalPages());
    }

    public void deleteDevice(UUID id) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        deviceRepository.delete(device);
    }

    private DeviceResponse toResponse(Device device) {
        return new DeviceResponse(
                device.getId(),
                device.getName(),
                device.getManufacturer(),
                device.getModel(),
                device.getSerialNumber(),
                device.getFirmwareVersion(),
                device.getStatus(),
                device.getOrganizationId(),
                device.getHierarchyNodeId(),
                device.getDeviceTemplateId(),
                device.getCreatedAt(),
                device.getUpdatedAt());
    }
}
