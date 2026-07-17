package com.joshsoll.telemetry.platform.exception;

public record ErrorResponse(
                int status,
                String message) {
}