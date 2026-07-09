package com.joshsoll.telemetry.platform.hierarchy.dto;

import java.util.UUID;

import com.joshsoll.telemetry.platform.hierarchy.constants.HierarchyNodeConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateHierarchyNodeRequest {
    @NotBlank
    @Size(min = HierarchyNodeConstants.NAME_MIN_LENGTH, max = HierarchyNodeConstants.NAME_MAX_LENGTH)
    private String name;

    // optional
    private UUID parentNodeId;

    @NotNull
    private UUID organizationId;

    public CreateHierarchyNodeRequest() {
    }

    public CreateHierarchyNodeRequest(String name, UUID parentNodeId, UUID organizationId) {
        this.name = name;
        this.parentNodeId = parentNodeId;
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public UUID getParentNodeId() {
        return parentNodeId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

}