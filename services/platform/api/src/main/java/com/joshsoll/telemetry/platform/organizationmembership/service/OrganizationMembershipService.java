package com.joshsoll.telemetry.platform.organizationmembership.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.exception.UserDoesNotExistException;
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
                        CreateOrganizationMembershipRequest request) {
                // org check
                Organization organization = organizationRepository.findById(request.getOrganizationId())
                                .orElseThrow(() -> new OrganizationNotFoundException(request.getOrganizationId()));

                // user check
                User user = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new UserDoesNotExistException(request.getUserId()));

                // already exists
                if (organizationMembershipRepository.existsByOrganization_IdAndUser_Id(request.getOrganizationId(),
                                request.getUserId())) {
                        throw new OrganizationMembershipAlreadyExistsException(
                                        request.getOrganizationId(),
                                        request.getUserId());
                }

                // create membership
                OrganizationMembership membership = new OrganizationMembership(
                                organization,
                                user,
                                request.getRole(),
                                MembershipStatus.ACTIVE);

                // saved membership
                OrganizationMembership savedMembership = organizationMembershipRepository.save(membership);

                return toResponse(savedMembership);
        }

        public PagedApiResponse<OrganizationMembershipResponse> getOrganizationMemberships(
                        UUID organizationId,
                        int page,
                        int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<OrganizationMembership> organizationMemberships = organizationMembershipRepository
                                .findAllByOrganization_Id(
                                                organizationId,
                                                pageable);

                List<OrganizationMembershipResponse> responses = new ArrayList<>();

                for (OrganizationMembership organizationMembership : organizationMemberships) {
                        responses.add(toResponse(organizationMembership));
                }

                return new PagedApiResponse<>(
                                responses,
                                "",
                                page,
                                size,
                                organizationMemberships.getTotalElements(),
                                organizationMemberships.getTotalPages());
        }

        public OrganizationMembershipResponse getOrganizationMembership(
                        UUID organizationId,
                        UUID membershipId) {

                OrganizationMembership membership = organizationMembershipRepository
                                .findByIdAndOrganization_Id(
                                                membershipId,
                                                organizationId)
                                .orElseThrow(() -> new OrganizationMembershipNotFoundException(
                                                membershipId));

                return toResponse(membership);
        }

        private OrganizationMembershipResponse toResponse(OrganizationMembership organizationMembership) {
                return new OrganizationMembershipResponse(
                                organizationMembership.getId(),
                                organizationMembership.getOrganization().getId(),
                                organizationMembership.getUser().getId(),
                                organizationMembership.getRole(),
                                organizationMembership.getStatus(),
                                organizationMembership.getCreatedAt(),
                                organizationMembership.getUpdatedAt());
        }
}
