package com.joshsoll.telemetry.platform.health.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.common.api.ApiRoutes;
import com.joshsoll.telemetry.platform.health.HealthStatus;
import com.joshsoll.telemetry.platform.health.service.HealthService;

@RestController
@RequestMapping(ApiRoutes.API_V1 + "/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public HealthStatus getHealth() {
        return healthService.getHealth();
    }
}
