package com.joshsoll.telemetry.platform.seed.organization;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

@Component
public class OrganizationGenerator {

    private final OrganizationRepository organizationRepository;

    public OrganizationGenerator(
            OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Organization generate(String name, String slug) {
        Instant now = Instant.now();

        Organization organization = new Organization(
                name,
                slug,
                now,
                now);

        return organizationRepository.save(organization);
    }

    public void generate(int count) {
        generate(count, null);
    }

    public void generate(
            int count,
            List<String> names) {

        for (int i = 1; i <= count; i++) {

            String name = names != null && i <= names.size()
                    ? names.get(i - 1)
                    : "Organization " + i;

            String slug = name
                    .toLowerCase()
                    .replace(" ", "-");

            generate(name, slug);
        }
    }
}
