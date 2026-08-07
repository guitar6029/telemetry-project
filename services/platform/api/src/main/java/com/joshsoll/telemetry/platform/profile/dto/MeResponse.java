package com.joshsoll.telemetry.platform.profile.dto;

import java.util.UUID;

public record MeResponse(
                UUID lastOrganizationUsed,
                String firstName,
                String lastName,
                String email,
                String avatarUrl) {

}
