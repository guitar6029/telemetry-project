package com.joshsoll.telemetry.platform.profile.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.organizationmembership.exceptions.OrganizationMembershipNotFoundException;
import com.joshsoll.telemetry.platform.organizationmembership.repository.OrganizationMembershipRepository;
import com.joshsoll.telemetry.platform.profile.dto.MeResponse;

@Service
public class ProfileService {

    private final OrganizationMembershipRepository organizationMembershipRepository;

    public ProfileService(
            OrganizationMembershipRepository organizationMembershipRepository) {
        this.organizationMembershipRepository = organizationMembershipRepository;
    }

    public MeResponse me(User authenticatedUser) {

        UUID organizationId = organizationMembershipRepository
                .findOrganizationIdByUserId(authenticatedUser.getId())
                .orElseThrow(() -> OrganizationMembershipNotFoundException.forUser(authenticatedUser.getId()));
        return toResponse(authenticatedUser, organizationId);
    }

    private MeResponse toResponse(User authenticatedUser, UUID organizationId) {
        return new MeResponse(
                organizationId,
                authenticatedUser.getFirstName(),
                authenticatedUser.getLastName(),
                authenticatedUser.getEmail(),
                authenticatedUser.getAvatarUrl());
    }
}
