package com.joshsoll.telemetry.platform.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.enums.PlatformRole;
import com.joshsoll.telemetry.platform.exception.security.PlatformAccessDeniedException;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.exception.OrganizationNotFoundException;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;
import com.joshsoll.telemetry.platform.organizationmembership.repository.OrganizationMembershipRepository;

@Service
public class AuthorizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMembershipRepository organizationMembershipRepository;

    public AuthorizationService(
            OrganizationRepository organizationRepository,
            OrganizationMembershipRepository organizationMembershipRepository) {
        this.organizationRepository = organizationRepository;
        this.organizationMembershipRepository = organizationMembershipRepository;
    }

    public Organization requireOrganizationAccess(
            User user,
            UUID organizationId) {

        if (user.getPlatformRole() == PlatformRole.SUPER_ADMIN) {
            return organizationRepository.findById(organizationId)
                    .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
        }

        return organizationMembershipRepository
                .findOrganizationByUserIdAndOrganizationId(
                        user.getId(),
                        organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
    }

    public void requireSuperAdmin(User user) {

        if (user.getPlatformRole() != PlatformRole.SUPER_ADMIN) {
            throw new PlatformAccessDeniedException();
        }
    }
}
