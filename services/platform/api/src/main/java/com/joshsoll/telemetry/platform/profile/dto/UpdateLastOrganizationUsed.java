package com.joshsoll.telemetry.platform.profile.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record UpdateLastOrganizationUsed(
        @NotNull UUID id) {

}
