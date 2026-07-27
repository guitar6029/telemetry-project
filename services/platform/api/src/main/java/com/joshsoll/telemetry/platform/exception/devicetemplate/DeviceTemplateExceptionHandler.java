package com.joshsoll.telemetry.platform.exception.devicetemplate;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateNotFoundException;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateOrganizationMismatchException;
import com.joshsoll.telemetry.platform.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class DeviceTemplateExceptionHandler {

    @ExceptionHandler(DeviceTemplateOrganizationMismatchException.class)
    public ResponseEntity<ErrorResponse> handleDeviceTemplateOrganizationMismatch(
            DeviceTemplateOrganizationMismatchException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(DeviceTemplateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeviceTemplateNotFound(
            DeviceTemplateNotFoundException ex,
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
