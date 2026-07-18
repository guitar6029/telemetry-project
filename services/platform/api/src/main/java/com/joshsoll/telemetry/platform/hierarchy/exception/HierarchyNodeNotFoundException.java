package com.joshsoll.telemetry.platform.hierarchy.exception;

import java.util.UUID;

public class HierarchyNodeNotFoundException extends RuntimeException {
    public HierarchyNodeNotFoundException(UUID hierarchyNodeId) {
        super("Hierarchy Node not found : " + hierarchyNodeId);
    }
}
