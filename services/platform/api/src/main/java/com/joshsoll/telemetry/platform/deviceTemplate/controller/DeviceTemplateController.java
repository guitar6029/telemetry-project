package com.joshsoll.telemetry.platform.deviceTemplate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.common.response.ApiResponse;
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

    /**
     * POST Create Template
     * 
     * GET List Templates
     * 
     * GET Get Template by ID
     */

    @PostMapping
    public ResponseEntity<ApiResponse<DeviceTemplateResponse>> createDeviceTemplate(
            @Valid @RequestBody CreateDeviceTemplateRequest request) {
        DeviceTemplateResponse deviceTemplate = deviceTemplateService.createDeviceTemplate(request);

        ApiResponse<DeviceTemplateResponse> response = new ApiResponse<>(deviceTemplate,
                "Device template created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
