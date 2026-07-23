package com.joshsoll.telemetry.platform.auth.service;

import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.config.JwtKeyProvider;
import com.joshsoll.telemetry.platform.auth.constants.JwtConstants;
import com.joshsoll.telemetry.platform.auth.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    private final JwtKeyProvider jwtKeyProvider;

    public JwtService(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    // generate
    public String generateAccessToken(User authenticatedUser) {

        Instant now = Instant.now();
        return Jwts.builder()
                .subject(authenticatedUser.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(createExpirationTime(now))
                .signWith(jwtKeyProvider.getPrivateKey())
                .compact();

    }

    private Date createExpirationTime(Instant now) {
        return Date.from(
                now.plus(Duration.ofMinutes(JwtConstants.ACCESS_TOKEN_EXPIRATION_MINUTES)));
    }

    public UUID extractSubject(String token) {
        Claims claims = extractClaims(token);

        return UUID.fromString(claims.getSubject());
    }

    private Claims extractClaims(String token) {
        PublicKey publicKey = jwtKeyProvider.getPublicKey();
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
