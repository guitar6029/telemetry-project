package com.joshsoll.telemetry.platform.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.seed.devicetemplate.DeviceTemplateGenerator;
import com.joshsoll.telemetry.platform.seed.hierarchy.HierarchyNodeGenerator;
import com.joshsoll.telemetry.platform.seed.organization.OrganizationGenerator;

@Component
@Profile("dev")
public class DatabaseSeeder implements CommandLineRunner {

        private final OrganizationGenerator organizationGenerator;
        private final DeviceTemplateGenerator deviceTemplateGenerator;
        private final HierarchyNodeGenerator hierarchyNodeGenerator;

        public DatabaseSeeder(
                        OrganizationGenerator organizationGenerator,
                        DeviceTemplateGenerator deviceTemplateGenerator,
                        HierarchyNodeGenerator hierarchyNodeGenerator

        ) {

                this.organizationGenerator = organizationGenerator;
                this.deviceTemplateGenerator = deviceTemplateGenerator;
                this.hierarchyNodeGenerator = hierarchyNodeGenerator;

        }

        @Override
        public void run(String... args) {
                Organization organization = organizationGenerator.generate(
                                "Organization 1",
                                "organization-1");

                deviceTemplateGenerator.generate(
                                5,
                                organization);

                HierarchyNode root = hierarchyNodeGenerator.generate(
                                "Hierarchy Node 1",
                                organization,
                                null);

                hierarchyNodeGenerator.generate(
                                10,
                                organization,
                                root);
        }

}
