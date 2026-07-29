package com.joshsoll.telemetry.platform.organizationmembership.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.enums.PlatformRole;
import com.joshsoll.telemetry.platform.auth.repository.UserRepository;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.exception.OrganizationNotFoundException;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;
import com.joshsoll.telemetry.platform.organizationmembership.dto.CreateOrganizationMembershipRequest;
import com.joshsoll.telemetry.platform.organizationmembership.dto.OrganizationMembershipResponse;
import com.joshsoll.telemetry.platform.organizationmembership.entity.OrganizationMembership;
import com.joshsoll.telemetry.platform.organizationmembership.enums.MembershipStatus;
import com.joshsoll.telemetry.platform.organizationmembership.exceptions.OrganizationMembershipAlreadyExistsException;
import com.joshsoll.telemetry.platform.organizationmembership.exceptions.OrganizationMembershipNotFoundException;
import com.joshsoll.telemetry.platform.organizationmembership.repository.OrganizationMembershipRepository;
import com.joshsoll.telemetry.platform.user.exception.UserNotFoundException;

@Service
public class OrganizationMembershipService {
        private final OrganizationMembershipRepository organizationMembershipRepository;
        private final OrganizationRepository organizationRepository;
        private final UserRepository userRepository;

        public OrganizationMembershipService(
                        OrganizationMembershipRepository organizationMembershipRepository,
                        OrganizationRepository organizationRepository,
                        UserRepository userRepository

        ) {
                this.organizationMembershipRepository = organizationMembershipRepository;
                this.organizationRepository = organizationRepository;
                this.userRepository = userRepository;
        }

        public OrganizationMembershipResponse createOrganizationMembership(
                        User user,
                        CreateOrganizationMembershipRequest request) {

                // Authorization + organization lookup
                Organization organization = getAccessibleOrganization(
                                user,
                                request.getOrganizationId());

                // user check
                User member = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

                // already exists
                if (organizationMembershipRepository.existsByOrganization_IdAndUser_Id(request.getOrganizationId(),
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

                Organization organization = getAccessibleOrganization(
                                user,
                                organizationId);

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

                Organization organization = getAccessibleOrganization(user, organizationId);
                OrganizationMembership membership = organizationMembershipRepository
                                .findByIdAndOrganization_Id(membershipId, organization.getId())
                                .orElseThrow(() -> new OrganizationMembershipNotFoundException(
                                                membershipId));

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

        private Organization getAccessibleOrganization(
                        User user,
                        UUID organizationId) {
                if (user.getPlatformRole() == PlatformRole.SUPER_ADMIN) {
                        return getOrganizationOrThrow(organizationId);

                }

                return organizationMembershipRepository
                                .findOrganizationByUserIdAndOrganizationId(
                                                user.getId(),
                                                organizationId)
                                .orElseThrow(() -> new OrganizationNotFoundException(
                                                organizationId));

        }

        private Organization getOrganizationOrThrow(UUID organizationId) {
                return organizationRepository.findById(organizationId)
                                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
        }
}
