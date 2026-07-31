package com.joshsoll.telemetry.platform.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.auth.constants.JwtConstants;
import com.joshsoll.telemetry.platform.auth.constants.UserConstants;
import com.joshsoll.telemetry.platform.auth.dto.LoginRequest;
import com.joshsoll.telemetry.platform.auth.dto.RegisterRequest;
import com.joshsoll.telemetry.platform.auth.exception.MissingAccessTokenException;
import com.joshsoll.telemetry.platform.auth.service.AuthService;
import com.joshsoll.telemetry.platform.common.api.ApiRoutes;
import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.security.util.CookieUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(ApiRoutes.API_V1 + "/auth")
public class AuthController {

    private final AuthService authService;
    private static final String RESOURCE_NAME = UserConstants.RESOURCE_NAME;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request) {
        authService.register(request);

        return ResponseFactory.created(RESOURCE_NAME);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .path("/")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(
                HttpHeaders.SET_COOKIE,
                cookie.toString());

        return ResponseFactory.<Void>ok(
                null,
                "Login Successful",
                headers);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        String token = CookieUtils.getCookieValue(request, JwtConstants.COOKIE_NAME)
                .orElseThrow(() -> new MissingAccessTokenException());

        authService.logout(token);

        return ResponseFactory.noContent();
    }

}
