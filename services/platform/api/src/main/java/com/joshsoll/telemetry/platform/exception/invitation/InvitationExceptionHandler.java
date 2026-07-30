package com.joshsoll.telemetry.platform.exception.invitation;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joshsoll.telemetry.platform.exception.ErrorResponse;
import com.joshsoll.telemetry.platform.invitation.exception.DuplicateInvitationException;
import com.joshsoll.telemetry.platform.invitation.exception.UserAlreadyOrganizationMemberException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class InvitationExceptionHandler {
    @ExceptionHandler(DuplicateInvitationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateInvitation(
            DuplicateInvitationException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(UserAlreadyOrganizationMemberException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyOrganizationMember(
            UserAlreadyOrganizationMemberException ex,
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
