package com.joshsoll.telemetry.platform.device.importer.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.common.api.ApiRoutes;
import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportHistoryResponse;
import com.joshsoll.telemetry.platform.device.importer.service.DeviceImportHistoryService;

@RestController
@RequestMapping(ApiRoutes.API_V1 + "/organizations/{organizationId}/imports")
public class DeviceImportHistoryController {

    private final DeviceImportHistoryService deviceImportHistoryService;

    public DeviceImportHistoryController(DeviceImportHistoryService deviceImportHistoryService) {
        this.deviceImportHistoryService = deviceImportHistoryService;
    }

    @GetMapping
    public ResponseEntity<PagedApiResponse<DeviceImportHistoryResponse>> getImports(
            @AuthenticationPrincipal User user,
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseFactory.ok(deviceImportHistoryService.getImports(user, organizationId, page, size));
    }

    @GetMapping("/{importId}")
    public ResponseEntity<ApiResponse<DeviceImportHistoryResponse>> getImport(
            @AuthenticationPrincipal User user,
            @PathVariable UUID organizationId,
            @PathVariable UUID importId) {
        return ResponseFactory.ok(deviceImportHistoryService.getImport(user, organizationId, importId), null);
    }
}
