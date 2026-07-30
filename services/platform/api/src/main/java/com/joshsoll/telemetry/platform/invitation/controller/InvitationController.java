package com.joshsoll.telemetry.platform.invitation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.common.api.ApiRoutes;
import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.invitation.constants.InvitationConstants;
import com.joshsoll.telemetry.platform.invitation.dto.InvitationRequest;
import com.joshsoll.telemetry.platform.invitation.dto.InvitationResponse;
import com.joshsoll.telemetry.platform.invitation.service.InvitationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiRoutes.API_V1 + "/organizations/{organizationId}/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvitationResponse>> sendInvitation(
            @AuthenticationPrincipal User user,
            @PathVariable UUID organizationId,
            @Valid @RequestBody InvitationRequest request) {

        InvitationResponse response = invitationService.sendInvitation(
                user,
                organizationId,
                request);

        return ResponseFactory.created(response, InvitationConstants.DOMAIN_NAME);
    }
}
