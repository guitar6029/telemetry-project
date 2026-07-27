package com.joshsoll.telemetry.platform.organization.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.enums.PlatformRole;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.organization.dto.CreateOrganizationRequest;
import com.joshsoll.telemetry.platform.organization.dto.OrganizationResponse;
import com.joshsoll.telemetry.platform.organization.dto.UpdateOrganizationRequest;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.exception.DuplicateOrganizationSlugException;
import com.joshsoll.telemetry.platform.organization.exception.OrganizationNotFoundException;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;
import com.joshsoll.telemetry.platform.organizationmembership.repository.OrganizationMembershipRepository;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMembershipRepository organizationMembershipRepository;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            OrganizationMembershipRepository organizationMembershipRepository) {
        this.organizationRepository = organizationRepository;
        this.organizationMembershipRepository = organizationMembershipRepository;
    }

    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {

        // check if the slug already exists
        if (organizationRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateOrganizationSlugException(request.getSlug());
        }

        Organization organization = new Organization(
                request.getName(),
                request.getSlug(),
                Instant.now(),
                Instant.now());

        Organization savedOrganization = organizationRepository.save(organization);

        return toResponse(savedOrganization);

    }

    public OrganizationResponse updateOrganization(UpdateOrganizationRequest request, UUID id) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException(id));

        if (!organization.getSlug().equals(request.getSlug())
                && organizationRepository.existsBySlug(request.getSlug())) {

            throw new DuplicateOrganizationSlugException(request.getSlug());
        }

        // if the content is the same
        if (organization.getName().equals(request.getName())
                && organization.getSlug().equals(request.getSlug())) {

            return toResponse(organization);
        }

        organization.setName(request.getName());
        organization.setSlug(request.getSlug());
        organization.setUpdatedAt(Instant.now());

        Organization savedOrganization = organizationRepository.save(organization);

        return toResponse(savedOrganization);

    }

    public OrganizationResponse getOrganizationById(UUID id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException(id));
        return toResponse(organization);
    }

    public PagedApiResponse<OrganizationResponse> getOrganizations(
            User user,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Organization> organizations = getAccessibleOrganizations(user, pageable);

        List<OrganizationResponse> responses = new ArrayList<>();

        for (Organization organization : organizations) {
            responses.add(toResponse(organization));
        }

        return new PagedApiResponse<>(
                responses,
                "",
                page,
                size,
                organizations.getTotalElements(),
                organizations.getTotalPages());
    }

    public void deleteOrganization(UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        organizationRepository.delete(organization);
    }

    private OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getCreatedAt(),
                organization.getUpdatedAt());
    }

    private Page<Organization> getAccessibleOrganizations(
            User user,
            Pageable pageable) {

        if (user.getPlatformRole() == PlatformRole.SUPER_ADMIN) {
            return organizationRepository.findAll(pageable);
        }

        return organizationMembershipRepository.findOrganizationsByUserId(
                user.getId(),
                pageable);
    }
}
