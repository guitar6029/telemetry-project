package com.joshsoll.telemetry.platform.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.seed.organization.OrganizationGenerator;

@Component
@Profile("dev")
public class DatabaseSeeder implements CommandLineRunner {

        private final OrganizationGenerator organizationGenerator;

        public DatabaseSeeder(
                        OrganizationGenerator organizationGenerator) {

                this.organizationGenerator = organizationGenerator;

        }

        @Override
        public void run(String... args) {
                organizationGenerator.generate(5);
        }

}
