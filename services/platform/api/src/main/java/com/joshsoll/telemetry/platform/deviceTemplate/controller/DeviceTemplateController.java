package com.joshsoll.telemetry.platform.deviceTemplate.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.deviceTemplate.dto.CreateDeviceTemplateRequest;
import com.joshsoll.telemetry.platform.deviceTemplate.dto.DeviceTemplateResponse;
import com.joshsoll.telemetry.platform.deviceTemplate.service.DeviceTemplateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/device-templates")
public class DeviceTemplateController {

    private final DeviceTemplateService deviceTemplateService;

    public DeviceTemplateController(DeviceTemplateService deviceTemplateService) {
        this.deviceTemplateService = deviceTemplateService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeviceTemplateResponse>> createDeviceTemplate(
            @Valid @RequestBody CreateDeviceTemplateRequest request) {
        DeviceTemplateResponse deviceTemplate = deviceTemplateService.createDeviceTemplate(request);

        ApiResponse<DeviceTemplateResponse> response = new ApiResponse<>(deviceTemplate,
                "Device template created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{deviceTemplateId}")
    public ResponseEntity<ApiResponse<DeviceTemplateResponse>> getDeviceTemplateById(
            @PathVariable UUID deviceTemplateId) {
        DeviceTemplateResponse deviceTemplateResponse = deviceTemplateService.getDeviceTemplateById(deviceTemplateId);
        ApiResponse<DeviceTemplateResponse> response = new ApiResponse<>(deviceTemplateResponse,
                "");

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedApiResponse<DeviceTemplateResponse>> getDeviceTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedApiResponse<DeviceTemplateResponse> deviceTemplateResponses = deviceTemplateService
                .getDeviceTemplates(page, size);
        return ResponseEntity.ok(deviceTemplateResponses);
    }

}
