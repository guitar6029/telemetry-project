package com.joshsoll.telemetry.platform.auth.exception;

import java.util.UUID;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(UUID id) {
        super("User is not found by this id: " + id);
    }
}
