package com.joshsoll.telemetry.platform.organizationmembership.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organizationmembership.dto.OrganizationMembershipResponse;
import com.joshsoll.telemetry.platform.organizationmembership.entity.OrganizationMembership;

public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, UUID> {
        boolean existsByOrganization_IdAndUser_Id(UUID organizationId, UUID userId);

        Optional<Organization> findOrganizationByUserIdAndOrganizationId(UUID userId, UUID organizationId);

        @Query("""
                        SELECT
                                om.id,
                                om.organization.id,
                                u.id,
                                u.firstName,
                                u.lastName,
                                u.email,
                                om.role,
                                om.status,
                                om.createdAt,
                                om.updatedAt
                        FROM OrganizationMembership om
                        JOIN om.user u
                        WHERE om.organization.id = :organizationId
                                                        """)
        Page<OrganizationMembershipResponse> findMembershipResponses(UUID organizationId, Pageable pageable);

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

        Optional<OrganizationMembership> findByOrganizationAndUser(
                        Organization organization,
                        User user);

        @Query("""
                        SELECT membership.organization.id
                        FROM OrganizationMembership membership
                        WHERE membership.user.id = :userId
                        """)
        Optional<UUID> findOrganizationIdByUserId(
                        @Param("userId") UUID userId);

        Optional<OrganizationMembership> findByUserIdAndOrganizationId(UUID userId, UUID organizationId);
}
