package com.joshsoll.telemetry.platform.organization.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.common.api.ApiRoutes;
import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.organization.constants.OrganizationConstants;
import com.joshsoll.telemetry.platform.organization.dto.CreateOrganizationRequest;
import com.joshsoll.telemetry.platform.organization.dto.OrganizationResponse;
import com.joshsoll.telemetry.platform.organization.dto.UpdateOrganizationRequest;
import com.joshsoll.telemetry.platform.organization.service.OrganizationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiRoutes.API_V1 + "/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public ResponseEntity<PagedApiResponse<OrganizationResponse>> getOrganizations(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedApiResponse<OrganizationResponse> organizations = organizationService.getOrganizations(user, page, size);

        return ResponseFactory.ok(organizations);
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(
            @AuthenticationPrincipal User user,
            @PathVariable UUID organizationId) {
        OrganizationResponse organization = organizationService.getOrganizationById(user, organizationId);
        return ResponseFactory.ok(organization, null);

    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationResponse organization = organizationService.createOrganization(user, request);
        return ResponseFactory.created(organization, OrganizationConstants.DOMAIN_NAME);
    }

    @PutMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @AuthenticationPrincipal User user,
            @PathVariable UUID organizationId,
            @Valid @RequestBody UpdateOrganizationRequest request) {
        OrganizationResponse organization = organizationService.updateOrganization(user, request, organizationId);
        return ResponseFactory.updated(organization, OrganizationConstants.DOMAIN_NAME);

    }

    @DeleteMapping("/{organizationId}")
    public ResponseEntity<Void> deleteOrganization(
            @AuthenticationPrincipal User user,
            @PathVariable UUID organizationId) {

        organizationService.deleteOrganization(user, organizationId);
        return ResponseEntity.noContent().build();
    }

}
