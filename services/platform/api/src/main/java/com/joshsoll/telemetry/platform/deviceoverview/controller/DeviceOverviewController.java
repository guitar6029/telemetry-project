package com.joshsoll.telemetry.platform.deviceoverview.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.common.api.ApiRoutes;
import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.deviceoverview.dto.DevicesOverviewResponse;
import com.joshsoll.telemetry.platform.deviceoverview.service.DeviceOverviewService;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(ApiRoutes.API_V1 + "organizations/{organizationId}/device-overview")
public class DeviceOverviewController {

    private final DeviceOverviewService deviceOverviewService;

    public DeviceOverviewController(
            DeviceOverviewService deviceOverviewService) {
        this.deviceOverviewService = deviceOverviewService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DevicesOverviewResponse>> getDevicesOverview(
            @AuthenticationPrincipal User user,
            @PathVariable UUID organizationId) {
        DevicesOverviewResponse deviceOverviewResponse = deviceOverviewService.getDevicesOverview(user,
                organizationId);

        return ResponseFactory.ok(deviceOverviewResponse, null);
    }

}
