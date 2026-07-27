package com.joshsoll.telemetry.platform.exception.hierarchy;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joshsoll.telemetry.platform.exception.ErrorResponse;
import com.joshsoll.telemetry.platform.hierarchy.exception.DuplicateHierarchyNodeException;
import com.joshsoll.telemetry.platform.hierarchy.exception.HierarchyNodeNotFoundException;
import com.joshsoll.telemetry.platform.hierarchy.exception.InvalidHierarchyNodeParentException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class HierarchyNodeExceptionHandler {

    @ExceptionHandler(HierarchyNodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHierarchyNodeNotFound(
            HierarchyNodeNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(InvalidHierarchyNodeParentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidHierarchyParent(
            InvalidHierarchyNodeParentException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(DuplicateHierarchyNodeException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateHierarchyNode(
            DuplicateHierarchyNodeException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }
}
