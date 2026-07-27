package com.joshsoll.telemetry.platform.exception.metricdefinition;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joshsoll.telemetry.platform.exception.ErrorResponse;
import com.joshsoll.telemetry.platform.metricdefinition.exception.DuplicateMetricDefinitionFieldException;
import com.joshsoll.telemetry.platform.metricdefinition.exception.MetricDefinitionNotFoundException;
import com.joshsoll.telemetry.platform.metricdefinition.exception.UnsupportedMetricDataTypeException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class MetricDefinitionExceptionHandler {
        @ExceptionHandler(MetricDefinitionNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleMetricDefinitionNotFound(
                        MetricDefinitionNotFoundException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponse(
                                                Instant.now(),
                                                HttpStatus.NOT_FOUND.value(),
                                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        @ExceptionHandler(DuplicateMetricDefinitionFieldException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateMetricDefinitionField(
                        DuplicateMetricDefinitionFieldException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new ErrorResponse(
                                                Instant.now(),
                                                HttpStatus.CONFLICT.value(),
                                                HttpStatus.CONFLICT.getReasonPhrase(),
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        @ExceptionHandler(UnsupportedMetricDataTypeException.class)
        public ResponseEntity<ErrorResponse> handleUnsupportedMetricDataType(
                        UnsupportedMetricDataTypeException ex,
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
