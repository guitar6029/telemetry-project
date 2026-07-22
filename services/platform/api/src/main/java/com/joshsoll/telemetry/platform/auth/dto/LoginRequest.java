package com.joshsoll.telemetry.platform.auth.dto;

import com.joshsoll.telemetry.platform.auth.constants.UserConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH)
    private String password;

    public LoginRequest() {

    }

    public LoginRequest(
            String email,
            String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
