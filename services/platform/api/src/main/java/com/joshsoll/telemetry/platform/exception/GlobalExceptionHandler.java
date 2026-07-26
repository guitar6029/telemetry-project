package com.joshsoll.telemetry.platform.exception;

import java.time.Instant;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.joshsoll.telemetry.platform.organization.exception.DuplicateOrganizationSlugException;
import com.joshsoll.telemetry.platform.organization.exception.OrganizationNotFoundException;
import com.joshsoll.telemetry.platform.organizationmembership.exceptions.OrganizationMembershipAlreadyExistsException;
import com.joshsoll.telemetry.platform.organizationmembership.exceptions.OrganizationMembershipNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(OrganizationNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleOrganizationNotFound(
                        OrganizationNotFoundException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponse(
                                                Instant.now(),
                                                HttpStatus.NOT_FOUND.value(),
                                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleOrganizationInvalidId(
                        MethodArgumentTypeMismatchException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResponse(
                                                Instant.now(),
                                                HttpStatus.BAD_REQUEST.value(),
                                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                                "Invalid organization ID format.",
                                                request.getRequestURI()));
        }

        @ExceptionHandler(DuplicateOrganizationSlugException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateOrganizationSlug(
                        DuplicateOrganizationSlugException ex,
                        HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new ErrorResponse(
                                                Instant.now(),
                                                HttpStatus.CONFLICT.value(),
                                                HttpStatus.CONFLICT.getReasonPhrase(),
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                List<ValidationError> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> new ValidationError(
                                                error.getField(),
                                                error.getDefaultMessage()))
                                .toList();

                return ResponseEntity.badRequest()
                                .body(new ValidationErrorResponse(
                                                Instant.now(),
                                                HttpStatus.BAD_REQUEST.value(),
                                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                                "Validation failed.",
                                                request.getRequestURI(),
                                                errors));
        }

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
