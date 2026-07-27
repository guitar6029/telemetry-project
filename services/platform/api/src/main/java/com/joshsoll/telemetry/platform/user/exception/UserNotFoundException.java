package com.joshsoll.telemetry.platform.user.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("User is not found by this id: " + id);
    }
}
