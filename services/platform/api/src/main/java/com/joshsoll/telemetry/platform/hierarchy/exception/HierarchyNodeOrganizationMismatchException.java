package com.joshsoll.telemetry.platform.hierarchy.exception;

public class HierarchyNodeOrganizationMismatchException extends RuntimeException {
    public HierarchyNodeOrganizationMismatchException() {
        super("Hierarchy node does not belong to the organization.");
    }
}
