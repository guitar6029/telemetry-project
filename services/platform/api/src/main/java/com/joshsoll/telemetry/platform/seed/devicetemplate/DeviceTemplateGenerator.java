package com.joshsoll.telemetry.platform.seed.devicetemplate;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.deviceTemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.deviceTemplate.repository.DeviceTemplateRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Component
public class DeviceTemplateGenerator {

    private final DeviceTemplateRepository deviceTemplateRepository;

    public DeviceTemplateGenerator(
            DeviceTemplateRepository deviceTemplateRepository) {
        this.deviceTemplateRepository = deviceTemplateRepository;
    }

    public DeviceTemplate generate(
            String name,
            String description,
            Organization organization) {

        Instant now = Instant.now();

        DeviceTemplate deviceTemplate = new DeviceTemplate(
                name,
                description,
                organization,
                false,
                now,
                now);

        return deviceTemplateRepository.save(deviceTemplate);
    }

    public void generate(
            int count,
            Organization organization) {

        generate(count, organization, null);
    }

    public void generate(
            int count,
            Organization organization,
            List<String> names) {

        for (int i = 1; i <= count; i++) {

            String name = names != null && i <= names.size()
                    ? names.get(i - 1)
                    : "Device Template " + i;

            generate(
                    name,
                    "Device Template Description " + i,
                    organization);
        }
    }
}
