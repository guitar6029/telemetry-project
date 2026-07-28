package com.joshsoll.telemetry.platform.exception.security;

public class PlatformAccessDeniedException
        extends RuntimeException {

    public PlatformAccessDeniedException() {
        super("You do not have permission to perform this action.");
    }
}
