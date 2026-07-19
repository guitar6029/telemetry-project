package com.joshsoll.telemetry.platform.organization.dto;

import com.joshsoll.telemetry.platform.organization.constants.OrganizationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateOrganizationRequest {
    @NotBlank
    @Size(min = OrganizationConstants.NAME_MIN_LENGTH, max = OrganizationConstants.NAME_MAX_LENGTH)
    private String name;

    @NotBlank
    @Size(min = OrganizationConstants.SLUG_MIN_LENGTH, max = OrganizationConstants.SLUG_MAX_LENGTH)
    private String slug;

    public UpdateOrganizationRequest() {

    }

    public UpdateOrganizationRequest(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

}
