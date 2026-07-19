package com.joshsoll.telemetry.platform.exception;

public record ValidationError(
        String field,
        String message) {
}
