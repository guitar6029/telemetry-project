package com.joshsoll.telemetry.platform.organizationmembership.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.organizationmembership.dto.CreateOrganizationMembershipRequest;
import com.joshsoll.telemetry.platform.organizationmembership.dto.OrganizationMembershipResponse;
import com.joshsoll.telemetry.platform.organizationmembership.service.OrganizationMembershipService;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/memberships")
public class OrganizationMembershipController {
    private final OrganizationMembershipService organizationMembershipService;
    private final String DOMAIN_NAME = "Organization membership";

    public OrganizationMembershipController(
            OrganizationMembershipService organizationMembershipService) {
        this.organizationMembershipService = organizationMembershipService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationMembershipResponse>> createOrganizationMembership(
            @PathVariable UUID organizationId,
            @Valid @RequestBody CreateOrganizationMembershipRequest request) {

        OrganizationMembershipResponse organizationMembership = organizationMembershipService
                .createOrganizationMembership(request);

        return ResponseFactory.created(organizationMembership, DOMAIN_NAME);
    }

    @GetMapping
    public ResponseEntity<PagedApiResponse<OrganizationMembershipResponse>> getOrganizationMemberships(
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedApiResponse<OrganizationMembershipResponse> organizationMemberships = organizationMembershipService
                .getOrganizationMemberships(
                        organizationId,
                        page,
                        size);

        return ResponseFactory.ok(organizationMemberships);
    }

    @GetMapping("/{membershipId}")
    public ResponseEntity<ApiResponse<OrganizationMembershipResponse>> getOrganizationMembership(
            @PathVariable UUID organizationId,
            @PathVariable UUID membershipId) {

        OrganizationMembershipResponse membership = organizationMembershipService.getOrganizationMembership(
                organizationId,
                membershipId);

        return ResponseFactory.ok(membership, null);
    }

}
