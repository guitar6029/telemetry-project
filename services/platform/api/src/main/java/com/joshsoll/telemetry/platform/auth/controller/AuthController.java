package com.joshsoll.telemetry.platform.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.auth.dto.RegisterRequest;
import com.joshsoll.telemetry.platform.auth.dto.RegisterResponse;
import com.joshsoll.telemetry.platform.auth.service.AuthService;
import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final String RESOURCE_NAME = "User";

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);

        return ResponseFactory.created(response, RESOURCE_NAME);
    }

}
