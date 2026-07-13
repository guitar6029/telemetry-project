package com.joshsoll.telemetry.platform.organization.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.organization.dto.CreateOrganizationRequest;
import com.joshsoll.telemetry.platform.organization.dto.OrganizationResponse;
import com.joshsoll.telemetry.platform.organization.service.OrganizationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public ResponseEntity<PagedApiResponse<OrganizationResponse>> getOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedApiResponse<OrganizationResponse> organizationResponses = organizationService.getOrganizations(page, size);

        return ResponseEntity.ok(organizationResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(@PathVariable UUID id) {
        OrganizationResponse organization = organizationService.getOrganizationById(id);
        ApiResponse<OrganizationResponse> response = new ApiResponse<>(organization, "");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationResponse organization = organizationService.createOrganization(request);

        ApiResponse<OrganizationResponse> response = new ApiResponse<>(organization,
                "Organization created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
