package com.joshsoll.telemetry.platform.seed.organizationmembership;

import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

import com.joshsoll.telemetry.platform.organizationmembership.entity.OrganizationMembership;
import com.joshsoll.telemetry.platform.organizationmembership.enums.MembershipStatus;
import com.joshsoll.telemetry.platform.organizationmembership.enums.OrganizationRole;
import com.joshsoll.telemetry.platform.organizationmembership.repository.OrganizationMembershipRepository;

@Component
public class OrganizationMembershipGenerator {

    private final OrganizationMembershipRepository organizationMembershipRepository;

    public OrganizationMembershipGenerator(
            OrganizationMembershipRepository organizationMembershipRepository) {
        this.organizationMembershipRepository = organizationMembershipRepository;
    }

    public OrganizationMembership generate(
            Organization organization,
            User user,
            OrganizationRole role,
            MembershipStatus status) {

        OrganizationMembership membership = new OrganizationMembership(
                organization,
                user,
                role,
                status);

        return organizationMembershipRepository.save(membership);
    }
}
