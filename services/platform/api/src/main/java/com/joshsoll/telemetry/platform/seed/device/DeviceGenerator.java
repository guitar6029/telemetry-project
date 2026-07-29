package com.joshsoll.telemetry.platform.seed.device;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.device.DeviceStatus;
import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Component
public class DeviceGenerator {

    private final DeviceRepository deviceRepository;

    public DeviceGenerator(
            DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device generate(
            String name,
            String manufacturer,
            String model,
            String serialNumber,
            String firmwareVersion,
            DeviceStatus status,
            Organization organization,
            HierarchyNode hierarchyNode,
            DeviceTemplate deviceTemplate) {

        return deviceRepository.findBySerialNumber(serialNumber)
                .orElseGet(() -> {

                    Instant now = Instant.now();

                    Device device = new Device(
                            name,
                            manufacturer,
                            model,
                            serialNumber,
                            firmwareVersion,
                            status,
                            organization,
                            hierarchyNode,
                            deviceTemplate,
                            now,
                            now);

                    return deviceRepository.save(device);
                });
    }

    public void generate(
            int count,
            Organization organization,
            HierarchyNode hierarchyNode,
            DeviceTemplate deviceTemplate) {

        for (int i = 1; i <= count; i++) {
            generate(
                    "Device " + i,
                    "Manufacturer " + i,
                    "Model " + i,
                    "SN-" + i,
                    "1.0." + i,
                    DeviceStatus.OFFLINE,
                    organization,
                    hierarchyNode,
                    deviceTemplate);
        }
    }
}
