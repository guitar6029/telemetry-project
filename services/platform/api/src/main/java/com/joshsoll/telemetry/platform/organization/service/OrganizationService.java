package com.joshsoll.telemetry.platform.organization.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.organization.dto.CreateOrganizationRequest;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Organization createOrganization(CreateOrganizationRequest request) {

        Organization organization = new Organization("Company Test", "company-test", Instant.now(), Instant.now());

        // later call organizationRepository.save(organization);
        return organization;
    }
}
