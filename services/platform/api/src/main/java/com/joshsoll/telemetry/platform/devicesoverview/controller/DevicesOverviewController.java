package com.joshsoll.telemetry.platform.devicesoverview.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.common.api.ApiRoutes;
import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.devicesoverview.dto.DevicesSummaryResponse;
import com.joshsoll.telemetry.platform.devicesoverview.service.DevicesOverviewService;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(ApiRoutes.API_V1 + "/organizations/{organizationId}/devices-overview")
public class DevicesOverviewController {

    private final DevicesOverviewService devicesOverviewService;

    public DevicesOverviewController(
            DevicesOverviewService devicesOverviewService) {
        this.devicesOverviewService = devicesOverviewService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DevicesSummaryResponse>> getDevicesOverview(
            @AuthenticationPrincipal User user,
            @PathVariable UUID organizationId) {
        DevicesSummaryResponse devicesSummaryResponse = devicesOverviewService.getDevicesSummary(user,
                organizationId);

        return ResponseFactory.ok(devicesSummaryResponse, null);
    }

}
