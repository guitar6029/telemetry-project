package com.joshsoll.telemetry.platform.hierarchy.exception;

public class DuplicateHierarchyNodeException extends RuntimeException {

    public DuplicateHierarchyNodeException() {
        super("Hierarchy node already exists.");
    }
}
