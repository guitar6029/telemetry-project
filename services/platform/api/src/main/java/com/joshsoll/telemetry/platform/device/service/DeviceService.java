package com.joshsoll.telemetry.platform.device.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.device.dto.CreateDeviceRequest;
import com.joshsoll.telemetry.platform.device.dto.DeviceResponse;
import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;
import com.joshsoll.telemetry.platform.organization.service.OrganizationService;

@Service
public class DeviceService {
    private final OrganizationService organizationService;
    private final DeviceRepository deviceRepository;
    private final OrganizationRepository organizationRepository;

    public DeviceService(DeviceRepository deviceRepository, OrganizationService organizationService,
            OrganizationRepository organizationRepository) {
        this.deviceRepository = deviceRepository;
        this.organizationService = organizationService;
        this.organizationRepository = organizationRepository;
    }

    // create
    public DeviceResponse createDevice(CreateDeviceRequest request) {

        Instant now = Instant.now();

        Organization organization = organizationRepository.findById(request.getOrganizationId()).orElseThrow();

        Device device = new Device(request.getName(), request.getModel(), request.getSerialNumber(),
                organization, now, now);

        Device savedDevice = deviceRepository.save(device);

        return new DeviceResponse(savedDevice.getId(), savedDevice.getName(), savedDevice.getModel(),
                savedDevice.getSerialNumber(), savedDevice.getCreatedAt(), savedDevice.getUpdatedAt());
    }

    // get device by id
    public DeviceResponse getDeviceById(UUID id){
        Device device = deviceRepository.findById(id).orElseThrow();
        return new DeviceResponse(
            device.getId(),
            device.getName(),
            device.getModel(),
            device.getSerialNumber(),
            device.getCreatedAt(),
            device.getUpdatedAt()
        );
    }

    // list devices
}
