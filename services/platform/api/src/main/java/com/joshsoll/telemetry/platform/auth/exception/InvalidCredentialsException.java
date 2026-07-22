package com.joshsoll.telemetry.platform.auth.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid Credentials.");
    }
}
