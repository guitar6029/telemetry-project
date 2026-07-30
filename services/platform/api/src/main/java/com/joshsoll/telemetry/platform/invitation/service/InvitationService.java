package com.joshsoll.telemetry.platform.invitation.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.repository.UserRepository;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.invitation.dto.InvitationRequest;
import com.joshsoll.telemetry.platform.invitation.dto.InvitationResponse;
import com.joshsoll.telemetry.platform.invitation.entity.Invitation;
import com.joshsoll.telemetry.platform.invitation.enums.InvitationStatus;
import com.joshsoll.telemetry.platform.invitation.exception.DuplicateInvitationException;
import com.joshsoll.telemetry.platform.invitation.exception.UserAlreadyOrganizationMemberException;
import com.joshsoll.telemetry.platform.invitation.repository.InvitationRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organizationmembership.repository.OrganizationMembershipRepository;

@Service
public class InvitationService {

        private final InvitationRepository invitationRepository;
        private final AuthorizationService authorizationService;
        private final OrganizationMembershipRepository organizationMembershipRepository;
        private final UserRepository userRepository;

        public InvitationService(
                        InvitationRepository invitationRepository,
                        AuthorizationService authorizationService,
                        OrganizationMembershipRepository organizationMembershipRepository,
                        UserRepository userRepository) {

                this.invitationRepository = invitationRepository;
                this.authorizationService = authorizationService;
                this.organizationMembershipRepository = organizationMembershipRepository;
                this.userRepository = userRepository;
        }

        public InvitationResponse sendInvitation(
                        User user,
                        UUID organizationId,
                        InvitationRequest request) {

                Organization organization = authorizationService.requireOrganizationAccess(
                                user,
                                organizationId);

                validateInvitation(
                                organization,
                                request);

                Invitation invitation = new Invitation(
                                organization.getId(),
                                request.email(),
                                request.role(),
                                InvitationStatus.PENDING);

                Invitation savedInvitation = invitationRepository.save(invitation);

                return toResponse(savedInvitation);
        }

        private void validateInvitation(
                        Organization organization,
                        InvitationRequest request) {

                ensureInvitationDoesNotExist(
                                organization.getId(),
                                request.email());

                ensureUserIsNotAlreadyMember(
                                organization.getId(),
                                request.email());
        }

        private void ensureInvitationDoesNotExist(
                        UUID organizationId,
                        String email) {

                if (invitationRepository.existsByOrganizationIdAndEmailAndStatus(
                                organizationId,
                                email,
                                InvitationStatus.PENDING)) {

                        throw new DuplicateInvitationException(email);
                }
        }

        private void ensureUserIsNotAlreadyMember(
                        UUID organizationId,
                        String email) {

                User existingUser = userRepository.findByEmail(email)
                                .orElse(null);

                if (existingUser != null
                                && organizationMembershipRepository.existsByOrganization_IdAndUser_Id(
                                                organizationId,
                                                existingUser.getId())) {

                        throw new UserAlreadyOrganizationMemberException();
                }
        }

        private InvitationResponse toResponse(
                        Invitation invitation) {

                return new InvitationResponse(
                                invitation.getEmail(),
                                invitation.getRole(),
                                invitation.getCreatedAt());
        }
}
