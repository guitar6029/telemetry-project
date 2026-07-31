package com.joshsoll.telemetry.platform.auth.exception;

public class MissingAccessTokenException extends RuntimeException {
    public MissingAccessTokenException() {
        super("Authentication token is missing.");
    }
}
