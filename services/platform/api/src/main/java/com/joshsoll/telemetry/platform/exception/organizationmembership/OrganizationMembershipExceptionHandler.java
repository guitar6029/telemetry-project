package com.joshsoll.telemetry.platform.exception.organizationmembership;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joshsoll.telemetry.platform.exception.ErrorResponse;
import com.joshsoll.telemetry.platform.organizationmembership.exceptions.OrganizationMembershipAlreadyExistsException;
import com.joshsoll.telemetry.platform.organizationmembership.exceptions.OrganizationMembershipNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class OrganizationMembershipExceptionHandler {
        @ExceptionHandler(OrganizationMembershipAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleOrganizationMembershipAlreadyExists(
                        OrganizationMembershipAlreadyExistsException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new ErrorResponse(
                                                Instant.now(),
                                                HttpStatus.CONFLICT.value(),
                                                HttpStatus.CONFLICT.getReasonPhrase(),
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        @ExceptionHandler(OrganizationMembershipNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleOrganizationMembershipNotFound(
                        OrganizationMembershipNotFoundException ex,
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
