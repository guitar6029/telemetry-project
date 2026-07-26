package com.joshsoll.telemetry.platform.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.seed.devicetemplate.DeviceTemplateGenerator;
import com.joshsoll.telemetry.platform.seed.organization.OrganizationGenerator;

@Component
@Profile("dev")
public class DatabaseSeeder implements CommandLineRunner {

        private final OrganizationGenerator organizationGenerator;
        private final DeviceTemplateGenerator deviceTemplateGenerator;

        public DatabaseSeeder(
                        OrganizationGenerator organizationGenerator,
                        DeviceTemplateGenerator deviceTemplateGenerator

        ) {

                this.organizationGenerator = organizationGenerator;
                this.deviceTemplateGenerator = deviceTemplateGenerator;

        }

        @Override
        public void run(String... args) {
                Organization organization = organizationGenerator.generate(
                                "Organization 1",
                                "organization-1");

                deviceTemplateGenerator.generate(
                                5,
                                organization);
        }

}
