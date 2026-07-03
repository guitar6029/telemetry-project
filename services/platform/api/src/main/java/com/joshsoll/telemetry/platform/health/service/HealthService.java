package com.joshsoll.telemetry.platform.health.service;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.health.HealthStatus;

@Service
public class HealthService {

    public HealthStatus getHealth() {
        return HealthStatus.UP;
    }
}
