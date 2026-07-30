package com.joshsoll.telemetry.platform.invitation.exception;

public class DuplicateInvitationException extends RuntimeException {
    public DuplicateInvitationException(String email) {
        super("Invitation has already been sent to:" + email);
    }
}
