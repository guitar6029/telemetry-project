package com.joshsoll.telemetry.platform.exception.security;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joshsoll.telemetry.platform.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class SecurityExceptionHandler {
    @ExceptionHandler(PlatformAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handlePlatformAccessDenied(
            PlatformAccessDeniedException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }
}
