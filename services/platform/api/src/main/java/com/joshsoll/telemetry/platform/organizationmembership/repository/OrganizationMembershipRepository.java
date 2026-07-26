package com.joshsoll.telemetry.platform.organizationmembership.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.organizationmembership.entity.OrganizationMembership;

public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, UUID> {
    boolean existsByOrganization_IdAndUser_Id(UUID organizationId, UUID userId);
}
