package com.joshsoll.telemetry.platform.profile.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.repository.UserRepository;
import com.joshsoll.telemetry.platform.profile.dto.MeResponse;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(
            UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MeResponse me(User authenticatedUser) {
        return toResponse(authenticatedUser);
    }

    public MeResponse updateLastOrganizationUsed(User authenticatedUser, UUID lastOrganizationUsed) {

        authenticatedUser.updateLastOrganizationUsed(lastOrganizationUsed);

        User updatedUser = userRepository.save(authenticatedUser);

        return toResponse(updatedUser);
    }

    private MeResponse toResponse(User authenticatedUser) {
        return new MeResponse(
                authenticatedUser.getLastOrganizationUsed(),
                authenticatedUser.getFirstName(),
                authenticatedUser.getLastName(),
                authenticatedUser.getEmail(),
                authenticatedUser.getAvatarUrl());
    }
}
