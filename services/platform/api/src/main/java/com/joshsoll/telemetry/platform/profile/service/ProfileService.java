package com.joshsoll.telemetry.platform.profile.service;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.profile.dto.MeResponse;

@Service
public class ProfileService {

    public MeResponse me(User authenticatedUser) {

        return toResponse(authenticatedUser);
    }

    private MeResponse toResponse(User authenticatedUser) {
        return new MeResponse(
                authenticatedUser.getId(),
                authenticatedUser.getFirstName(),
                authenticatedUser.getLastName(),
                authenticatedUser.getEmail(),
                authenticatedUser.getAvatarUrl());
    }
}
