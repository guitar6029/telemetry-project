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
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.hierarchy.repository.HierarchyNodeRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final OrganizationRepository organizationRepository;
    private final HierarchyNodeRepository hierarchyNodeRepository;

    public DeviceService(DeviceRepository deviceRepository,
            OrganizationRepository organizationRepository, HierarchyNodeRepository hierarchyNodeRepository) {
        this.deviceRepository = deviceRepository;
        this.organizationRepository = organizationRepository;
        this.hierarchyNodeRepository = hierarchyNodeRepository;
    }

    // create
    public DeviceResponse createDevice(CreateDeviceRequest request) {

        Instant now = Instant.now();

        // find organization
        Organization organization = organizationRepository.findById(request.getOrganizationId()).orElseThrow();

        // find hierarchy node
        HierarchyNode hierarchyNode = hierarchyNodeRepository.findById(request.getHierarchyNodeId()).orElseThrow();

        // Validate hierarchy belongs to organization
        if (!hierarchyNode.getOrganization().getId().equals(organization.getId())) {
            throw new IllegalArgumentException("Org id is incorrect, try again");
        }

        // Validate serial number uniqueness
        if (deviceRepository.existsByOrganizationAndSerialNumber(organization,
                request.getSerialNumber())) {
            throw new IllegalArgumentException(
                    "A device with this serial number already exists in the organization.");
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
                now,
                now);

        Device savedDevice = deviceRepository.save(device);

        return toResponse(savedDevice);
    }

    // get device by id
    public DeviceResponse getDeviceById(UUID id) {
        Device device = deviceRepository.findById(id).orElseThrow();
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
                device.getCreatedAt(),
                device.getUpdatedAt());
    }
}
