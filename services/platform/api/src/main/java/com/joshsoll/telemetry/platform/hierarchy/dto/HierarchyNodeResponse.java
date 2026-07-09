package com.joshsoll.telemetry.platform.hierarchy.dto;

import java.util.UUID;

public record HierarchyNodeResponse(
                String name,
                UUID id,
                UUID parentNodeId,
                UUID organizationId) {

}
