package com.joshsoll.telemetry.platform.invitation.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.invitation.entity.Invitation;
import com.joshsoll.telemetry.platform.invitation.enums.InvitationStatus;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    boolean existsByOrganizationIdAndEmailAndStatus(
            UUID organizationId,
            String email,
            InvitationStatus status);
}
