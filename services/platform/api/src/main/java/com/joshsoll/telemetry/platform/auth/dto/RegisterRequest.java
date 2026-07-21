package com.joshsoll.telemetry.platform.auth.dto;

import com.joshsoll.telemetry.platform.auth.constants.UserConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank
    @Size(min = UserConstants.FIRST_NAME_MIN_LENGTH, max = UserConstants.FIRST_NAME_MAX_LENGTH)
    private String firstName;

    @NotBlank
    @Size(min = UserConstants.LAST_NAME_MIN_LENGTH, max = UserConstants.LAST_NAME_MAX_LENGTH)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = UserConstants.EMAIL_MAX_LENGTH)
    private String email;

    @NotBlank
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH)
    private String password;

    public RegisterRequest() {

    }

    public RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
