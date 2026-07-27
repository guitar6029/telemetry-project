package com.joshsoll.telemetry.platform.exception.user;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joshsoll.telemetry.platform.exception.ErrorResponse;
import com.joshsoll.telemetry.platform.user.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserDoesNotExist(
            UserNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }
}
