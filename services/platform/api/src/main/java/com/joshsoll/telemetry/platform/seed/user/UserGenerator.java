package com.joshsoll.telemetry.platform.seed.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.auth.constants.UserConstants;
import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.enums.PlatformRole;
import com.joshsoll.telemetry.platform.auth.repository.UserRepository;

@Component
public class UserGenerator {

    private static final String DEFAULT_AVATAR_URL = UserConstants.DEFAULT_AVATAR_URL;
    private static final String DEFAULT_PASSWORD = "password123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserGenerator(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User generate(
            String firstName,
            String lastName,
            String email,
            String password,
            String avatarUrl,
            PlatformRole role) {

        return userRepository.findByEmail(email)
                .orElseGet(() -> {

                    User user = new User(
                            firstName,
                            lastName,
                            email,
                            passwordEncoder.encode(password),
                            avatarUrl,
                            role);

                    return userRepository.save(user);
                });
    }

    public void generate(int count) {

        for (int i = 1; i <= count; i++) {
            generate(
                    "User" + i,
                    "Test" + i,
                    "user" + i + "@example.com",
                    DEFAULT_PASSWORD,
                    DEFAULT_AVATAR_URL,
                    PlatformRole.USER);
        }
    }
}
