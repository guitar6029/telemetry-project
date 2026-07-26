package com.joshsoll.telemetry.platform.organizationmembership.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.organizationmembership.entity.OrganizationMembership;

public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, UUID> {
    boolean existsByOrganization_IdAndUser_Id(UUID organizationId, UUID userId);

    Page<OrganizationMembership> findAllByOrganization_Id(UUID organizationId, Pageable pageable);

    Optional<OrganizationMembership> findByIdAndOrganization_Id(
            UUID membershipId,
            UUID organizationId);
}
