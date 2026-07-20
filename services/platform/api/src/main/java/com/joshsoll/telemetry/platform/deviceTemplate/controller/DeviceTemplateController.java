package com.joshsoll.telemetry.platform.deviceTemplate.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.deviceTemplate.dto.CreateDeviceTemplateRequest;
import com.joshsoll.telemetry.platform.deviceTemplate.dto.DeviceTemplateResponse;
import com.joshsoll.telemetry.platform.deviceTemplate.dto.UpdateDeviceTemplateRequest;
import com.joshsoll.telemetry.platform.deviceTemplate.service.DeviceTemplateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/device-templates")
public class DeviceTemplateController {

    private final DeviceTemplateService deviceTemplateService;
    private final String DOMAIN_NAME = "Device Template";

    public DeviceTemplateController(DeviceTemplateService deviceTemplateService) {
        this.deviceTemplateService = deviceTemplateService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeviceTemplateResponse>> createDeviceTemplate(
            @Valid @RequestBody CreateDeviceTemplateRequest request) {
        DeviceTemplateResponse deviceTemplate = deviceTemplateService.createDeviceTemplate(request);

        return ResponseFactory.created(deviceTemplate, DOMAIN_NAME);
    }

    @GetMapping("/{deviceTemplateId}")
    public ResponseEntity<ApiResponse<DeviceTemplateResponse>> getDeviceTemplateById(
            @PathVariable UUID deviceTemplateId) {
        DeviceTemplateResponse deviceTemplateResponse = deviceTemplateService.getDeviceTemplateById(deviceTemplateId);

        return ResponseFactory.ok(deviceTemplateResponse, null);
    }

    @GetMapping
    public ResponseEntity<PagedApiResponse<DeviceTemplateResponse>> getDeviceTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedApiResponse<DeviceTemplateResponse> deviceTemplateResponses = deviceTemplateService
                .getDeviceTemplates(page, size);
        return ResponseFactory.ok(deviceTemplateResponses);
    }

    @PutMapping("/{deviceTemplateId}")
    public ResponseEntity<ApiResponse<DeviceTemplateResponse>> updateDeviceTemplate(
            @PathVariable UUID deviceTemplateId,
            @Valid @RequestBody UpdateDeviceTemplateRequest request) {
        DeviceTemplateResponse deviceTemplateResponse = deviceTemplateService.updateDeviceTemplate(request,
                deviceTemplateId);

        return ResponseFactory.updated(deviceTemplateResponse, DOMAIN_NAME);
    }

    @DeleteMapping("/{deviceTemplateId}")
    public ResponseEntity<Void> deleteDeviceTemplate(
            @PathVariable UUID deviceTemplateId) {
        deviceTemplateService.deleteDeviceTemplate(deviceTemplateId);
        return ResponseEntity.noContent().build();
    }

}
