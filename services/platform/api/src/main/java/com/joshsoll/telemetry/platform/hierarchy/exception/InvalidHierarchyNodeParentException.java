package com.joshsoll.telemetry.platform.hierarchy.exception;

public class InvalidHierarchyNodeParentException extends RuntimeException {
    public InvalidHierarchyNodeParentException() {
        super("Parent node does not belong to this organization.");
    }
}
