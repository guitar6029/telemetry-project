package com.joshsoll.telemetry.platform.device.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.common.api.ApiRoutes;
import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.device.constants.DeviceConstants;
import com.joshsoll.telemetry.platform.device.dto.CreateDeviceRequest;
import com.joshsoll.telemetry.platform.device.dto.DeviceResponse;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportResponse;
import com.joshsoll.telemetry.platform.device.importer.service.DeviceImportService;
import com.joshsoll.telemetry.platform.device.service.DeviceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiRoutes.API_V1 + "devices")
public class DeviceController {
    private final DeviceService deviceService;
    private final String DOMAIN_NAME = DeviceConstants.DOMAIN_NAME;
    private final DeviceImportService deviceImportService;

    public DeviceController(
            DeviceService deviceService,
            DeviceImportService deviceImportService) {
        this.deviceService = deviceService;
        this.deviceImportService = deviceImportService;
    }

    @GetMapping
    public ResponseEntity<PagedApiResponse<DeviceResponse>> getDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedApiResponse<DeviceResponse> deviceResponses = deviceService.getDevices(page, size);
        return ResponseFactory.ok(deviceResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<DeviceResponse>> getDeviceById(@PathVariable UUID id) {
        DeviceResponse deviceResponse = deviceService.getDeviceById(id);
        return ResponseFactory.ok(deviceResponse, null);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeviceResponse>> createDevice(
            @Valid @RequestBody CreateDeviceRequest request) {
        DeviceResponse device = deviceService.createDevice(request);
        return ResponseFactory.created(device, DOMAIN_NAME);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(
            @PathVariable UUID id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{organizationId}/{templateId}/{hierarchyNodeId}/import")
    public ResponseEntity<ApiResponse<DeviceImportResponse>> importDevices(
            @AuthenticationPrincipal User user,
            @PathVariable UUID organizationId,
            @PathVariable UUID templateId,
            @PathVariable UUID hierarchyNodeId,
            @RequestParam MultipartFile file) {
        DeviceImportResponse response = deviceImportService.importDevices(
                user,
                organizationId,
                templateId,
                hierarchyNodeId,
                file);

        return ResponseFactory.accepted(response, DOMAIN_NAME);
    }
}
