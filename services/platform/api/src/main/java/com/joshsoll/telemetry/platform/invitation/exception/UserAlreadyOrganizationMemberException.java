package com.joshsoll.telemetry.platform.invitation.exception;

public class UserAlreadyOrganizationMemberException extends RuntimeException {
    public UserAlreadyOrganizationMemberException() {
        super("User is already a member of this organization.");
    }
}
