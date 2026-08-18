package com.joshsoll.telemetry.platform.exception.device;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joshsoll.telemetry.platform.device.exception.DeviceImportInvalidException;
import com.joshsoll.telemetry.platform.device.exception.DeviceNotFoundException;
import com.joshsoll.telemetry.platform.device.exception.DuplicateDeviceSerialNumberException;
import com.joshsoll.telemetry.platform.device.importer.exception.DeviceImportArtifactException;
import com.joshsoll.telemetry.platform.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class DeviceExceptionHandler {
        @ExceptionHandler(DeviceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleDeviceNotFound(
                        DeviceNotFoundException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponse(
                                                Instant.now(),
                                                HttpStatus.NOT_FOUND.value(),
                                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        @ExceptionHandler(DuplicateDeviceSerialNumberException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateDeviceSerialNumber(
                        DuplicateDeviceSerialNumberException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new ErrorResponse(
                                                Instant.now(),
                                                HttpStatus.CONFLICT.value(),
                                                HttpStatus.CONFLICT.getReasonPhrase(),
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        @ExceptionHandler(DeviceImportInvalidException.class)
        public ResponseEntity<ErrorResponse> handleDeviceImportInvalid(
                        DeviceImportInvalidException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResponse(
                                                Instant.now(),
                                                HttpStatus.BAD_REQUEST.value(),
                                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        @ExceptionHandler(DeviceImportArtifactException.class)
        public ResponseEntity<ErrorResponse> handleDeviceImportArtifact(
                        DeviceImportArtifactException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponse(
                                                Instant.now(),
                                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }
}
