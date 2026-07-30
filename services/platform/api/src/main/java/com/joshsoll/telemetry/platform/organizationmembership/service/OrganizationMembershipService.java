package com.joshsoll.telemetry.platform.organizationmembership.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.repository.UserRepository;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;
import com.joshsoll.telemetry.platform.organizationmembership.dto.CreateOrganizationMembershipRequest;
import com.joshsoll.telemetry.platform.organizationmembership.dto.OrganizationMembershipResponse;
import com.joshsoll.telemetry.platform.organizationmembership.dto.UpdateOrganizationMembershipRequest;
import com.joshsoll.telemetry.platform.organizationmembership.entity.OrganizationMembership;
import com.joshsoll.telemetry.platform.organizationmembership.enums.MembershipStatus;
import com.joshsoll.telemetry.platform.organizationmembership.exceptions.OrganizationMembershipAlreadyExistsException;
import com.joshsoll.telemetry.platform.organizationmembership.exceptions.OrganizationMembershipNotFoundException;
import com.joshsoll.telemetry.platform.organizationmembership.repository.OrganizationMembershipRepository;
import com.joshsoll.telemetry.platform.user.exception.UserNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class OrganizationMembershipService {
        private final OrganizationMembershipRepository organizationMembershipRepository;

        private final UserRepository userRepository;
        private final AuthorizationService authorizationService;

        public OrganizationMembershipService(
                        OrganizationMembershipRepository organizationMembershipRepository,
                        OrganizationRepository organizationRepository,
                        UserRepository userRepository,
                        AuthorizationService authorizationService

        ) {
                this.organizationMembershipRepository = organizationMembershipRepository;
                this.userRepository = userRepository;
                this.authorizationService = authorizationService;
        }

        public OrganizationMembershipResponse createOrganizationMembership(
                        User user,
                        UUID organizationId,
                        CreateOrganizationMembershipRequest request) {

                Organization organization = authorizationService.requireOrganizationAccess(user, organizationId);

                // user check
                User member = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

                // already exists
                if (organizationMembershipRepository.existsByOrganization_IdAndUser_Id(organizationId,
                                request.getUserId())) {
                        throw new OrganizationMembershipAlreadyExistsException(
                                        organization.getId(),
                                        member.getId());
                }

                // create membership
                OrganizationMembership membership = new OrganizationMembership(
                                organization,
                                member,
                                request.getRole(),
                                MembershipStatus.ACTIVE);

                // saved membership
                OrganizationMembership savedMembership = organizationMembershipRepository.save(membership);

                return toResponse(savedMembership);
        }

        public PagedApiResponse<OrganizationMembershipResponse> getOrganizationMemberships(
                        User user,
                        UUID organizationId,
                        int page,
                        int size) {

                Pageable pageable = PageRequest.of(page, size);

                Organization organization = authorizationService.requireOrganizationAccess(user, organizationId);

                Page<OrganizationMembershipResponse> membershipPage = organizationMembershipRepository
                                .findMembershipResponses(
                                                organization.getId(),
                                                pageable);

                return new PagedApiResponse<>(
                                membershipPage.getContent(),
                                "",
                                page,
                                size,
                                membershipPage.getTotalElements(),
                                membershipPage.getTotalPages());
        }

        public OrganizationMembershipResponse getOrganizationMembership(
                        User user,
                        UUID organizationId,
                        UUID membershipId) {

                Organization organization = authorizationService.requireOrganizationAccess(user, organizationId);
                OrganizationMembership membership = organizationMembershipRepository
                                .findByIdAndOrganization_Id(membershipId, organization.getId())
                                .orElseThrow(() -> new OrganizationMembershipNotFoundException(
                                                membershipId));

                return toResponse(membership);
        }

        @Transactional
        public OrganizationMembershipResponse updateOrganizationMembership(
                        User user,
                        UUID organizationId,
                        UUID membershipId,
                        UpdateOrganizationMembershipRequest request) {
                Organization organization = authorizationService.requireOrganizationAccess(user, organizationId);
                OrganizationMembership membership = organizationMembershipRepository
                                .findByIdAndOrganization_Id(membershipId, organization.getId())
                                .orElseThrow(() -> new OrganizationMembershipNotFoundException(
                                                membershipId));

                membership.updateMembership(request.role(), request.status());

                return toResponse(membership);

        }

        private OrganizationMembershipResponse toResponse(OrganizationMembership organizationMembership) {
                return new OrganizationMembershipResponse(
                                organizationMembership.getId(),
                                organizationMembership.getOrganization().getId(),
                                organizationMembership.getUser().getId(),
                                organizationMembership.getUser().getFirstName(),
                                organizationMembership.getUser().getLastName(),
                                organizationMembership.getUser().getEmail(),
                                organizationMembership.getRole(),
                                organizationMembership.getStatus(),
                                organizationMembership.getCreatedAt(),
                                organizationMembership.getUpdatedAt());
        }
}
