package com.joshsoll.telemetry.platform.organization.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.organization.dto.CreateOrganizationRequest;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.service.OrganizationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Organization>> getOrganization(@PathVariable UUID id) {
        Organization organization = organizationService.getOrganization(id);
        ApiResponse<Organization> response = new ApiResponse<>(organization, "");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Organization>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request) {
        Organization organization = organizationService.createOrganization(request);

        ApiResponse<Organization> response = new ApiResponse<>(organization, "Organization created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
