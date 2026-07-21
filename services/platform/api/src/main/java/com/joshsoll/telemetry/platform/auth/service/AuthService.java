package com.joshsoll.telemetry.platform.auth.service;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.dto.RegisterRequest;
import com.joshsoll.telemetry.platform.auth.dto.RegisterResponse;
import com.joshsoll.telemetry.platform.auth.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResponse register(RegisterRequest request) {
        throw new UnsupportedOperationException();
    }
}
