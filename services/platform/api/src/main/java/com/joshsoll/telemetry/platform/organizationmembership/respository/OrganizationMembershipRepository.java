package com.joshsoll.telemetry.platform.organizationmembership.respository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.organizationmembership.entity.OrganizationMembership;

public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, UUID> {

}
