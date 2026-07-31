package com.joshsoll.telemetry.platform.auth.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class TokenRevocationService {

    private final Set<String> revokedTokens = new HashSet<>();

    public void revokeToken(String token) {

        revokedTokens.add(token);

    }

    public boolean isRevoked(String token) {
        return revokedTokens.contains(token);
    }
}
