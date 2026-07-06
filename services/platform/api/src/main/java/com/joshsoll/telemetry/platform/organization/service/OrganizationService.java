package com.joshsoll.telemetry.platform.organization.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.organization.dto.CreateOrganizationRequest;
import com.joshsoll.telemetry.platform.organization.dto.OrganizationResponse;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Organization createOrganization(CreateOrganizationRequest request) {

        Organization organization = new Organization(
                request.getName(),
                request.getSlug(),
                Instant.now(),
                Instant.now());

        return organizationRepository.save(organization);

    }

    public OrganizationResponse getOrganization(UUID id) {
        Organization organization = organizationRepository.findById(id).orElseThrow();
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getCreatedAt(),
                organization.getUpdatedAt());
    }
}
