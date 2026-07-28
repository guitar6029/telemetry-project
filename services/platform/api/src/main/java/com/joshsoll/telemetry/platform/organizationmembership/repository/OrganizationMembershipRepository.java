package com.joshsoll.telemetry.platform.organizationmembership.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organizationmembership.entity.OrganizationMembership;

public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, UUID> {
        boolean existsByOrganization_IdAndUser_Id(UUID organizationId, UUID userId);

        Optional<Organization> findOrganizationByUserIdAndOrganizationId(UUID userId, UUID organizationId);

        Page<OrganizationMembership> findAllByOrganization_Id(UUID organizationId, Pageable pageable);

        Optional<OrganizationMembership> findByIdAndOrganization_Id(
                        UUID membershipId,
                        UUID organizationId);

        @Query("""
                        SELECT membership.organization
                        FROM OrganizationMembership membership
                        WHERE membership.user.id = :userId
                        """)
        Page<Organization> findOrganizationsByUserId(
                        @Param("userId") UUID userId,
                        Pageable pageable);
}
