package com.joshsoll.telemetry.platform.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.auth.constants.UserConstants;
import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.device.DeviceStatus;
import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.metricdefinition.MetricDataType;
import com.joshsoll.telemetry.platform.metricdefinition.entity.MetricDefinition;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organizationmembership.enums.MembershipStatus;
import com.joshsoll.telemetry.platform.organizationmembership.enums.OrganizationRole;
import com.joshsoll.telemetry.platform.seed.device.DeviceGenerator;
import com.joshsoll.telemetry.platform.seed.devicetemplate.DeviceTemplateGenerator;
import com.joshsoll.telemetry.platform.seed.hierarchy.HierarchyNodeGenerator;
import com.joshsoll.telemetry.platform.seed.metricdefinition.MetricDefinitionGenerator;
import com.joshsoll.telemetry.platform.seed.metricvalue.MetricValueGenerator;
import com.joshsoll.telemetry.platform.seed.organization.OrganizationGenerator;
import com.joshsoll.telemetry.platform.seed.organizationmembership.OrganizationMembershipGenerator;
import com.joshsoll.telemetry.platform.seed.telemetryevent.TelemetryEventGenerator;
import com.joshsoll.telemetry.platform.seed.user.UserGenerator;
import com.joshsoll.telemetry.platform.telemetryevent.entity.TelemetryEvent;

@Component
@Profile("dev")
public class DatabaseSeeder implements CommandLineRunner {

        private final OrganizationGenerator organizationGenerator;
        private final DeviceTemplateGenerator deviceTemplateGenerator;
        private final HierarchyNodeGenerator hierarchyNodeGenerator;
        private final DeviceGenerator deviceGenerator;
        private final MetricDefinitionGenerator metricDefinitionGenerator;
        private final TelemetryEventGenerator telemetryEventGenerator;
        private final MetricValueGenerator metricValueGenerator;
        private final UserGenerator userGenerator;
        private final OrganizationMembershipGenerator organizationMembershipGenerator;

        public DatabaseSeeder(
                        OrganizationGenerator organizationGenerator,
                        DeviceTemplateGenerator deviceTemplateGenerator,
                        HierarchyNodeGenerator hierarchyNodeGenerator,
                        DeviceGenerator deviceGenerator,
                        MetricDefinitionGenerator metricDefinitionGenerator,
                        TelemetryEventGenerator telemetryEventGenerator,
                        MetricValueGenerator metricValueGenerator,
                        UserGenerator userGenerator,
                        OrganizationMembershipGenerator organizationMembershipGenerator

        ) {

                this.organizationGenerator = organizationGenerator;
                this.deviceTemplateGenerator = deviceTemplateGenerator;
                this.hierarchyNodeGenerator = hierarchyNodeGenerator;
                this.deviceGenerator = deviceGenerator;
                this.metricDefinitionGenerator = metricDefinitionGenerator;
                this.telemetryEventGenerator = telemetryEventGenerator;
                this.metricValueGenerator = metricValueGenerator;
                this.userGenerator = userGenerator;
                this.organizationMembershipGenerator = organizationMembershipGenerator;
        }

        @Override
        public void run(String... args) {

                // Organization
                Organization organization = organizationGenerator.generate(
                                "Organization 1",
                                "organization-1");

                // Device Templates: 5 total
                DeviceTemplate deviceTemplate = deviceTemplateGenerator.generate(
                                "Primary Device Template",
                                "Primary Device Template Description",
                                organization);

                deviceTemplateGenerator.generate(
                                4,
                                organization);

                // Hierarchy: 1 root + 10 children
                HierarchyNode root = hierarchyNodeGenerator.generate(
                                "Hierarchy Root",
                                organization,
                                null);

                hierarchyNodeGenerator.generate(
                                10,
                                organization,
                                root);

                // Devices: 10 total
                Device device = deviceGenerator.generate(
                                "Primary Device",
                                "Manufacturer",
                                "Model",
                                "SN-PRIMARY",
                                "1.0.0",
                                DeviceStatus.OFFLINE,
                                organization,
                                root,
                                deviceTemplate);

                deviceGenerator.generate(
                                9,
                                organization,
                                root,
                                deviceTemplate);

                // Metric Definitions: 5 total
                MetricDefinition metricDefinition = metricDefinitionGenerator.generate(
                                "Primary Metric",
                                "Primary generated metric",
                                "primary_metric",
                                MetricDataType.NUMBER,
                                "unit",
                                deviceTemplate);

                metricDefinitionGenerator.generate(
                                4,
                                deviceTemplate);

                // Telemetry Event
                TelemetryEvent telemetryEvent = telemetryEventGenerator.generate(
                                device,
                                "{\"value\":42}");

                // Metric Values
                metricValueGenerator.generate(
                                10,
                                telemetryEvent,
                                metricDefinition);

                User user = userGenerator.generate(
                                "Primary",
                                "User",
                                "primary@example.com",
                                "password123",
                                UserConstants.DEFAULT_AVATAR_URL);

                organizationMembershipGenerator.generate(
                                organization,
                                user,
                                OrganizationRole.ADMIN,
                                MembershipStatus.ACTIVE);
                // userGenerator.generate(9);
        }
}
