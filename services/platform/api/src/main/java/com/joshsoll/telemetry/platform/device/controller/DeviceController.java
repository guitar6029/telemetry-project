package com.joshsoll.telemetry.platform.device.controller;

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
import com.joshsoll.telemetry.platform.device.dto.CreateDeviceRequest;
import com.joshsoll.telemetry.platform.device.dto.DeviceResponse;
import com.joshsoll.telemetry.platform.device.service.DeviceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<PagedApiResponse<DeviceResponse>> getDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedApiResponse<DeviceResponse> deviceResponses = deviceService.getDevices(page, size);
        return ResponseEntity.ok(deviceResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<DeviceResponse>> getDeviceById(@PathVariable UUID id) {
        DeviceResponse device = deviceService.getDeviceById(id);
        ApiResponse<DeviceResponse> response = new ApiResponse<>(device, "");

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeviceResponse>> createDevice(
            @Valid @RequestBody CreateDeviceRequest request) {
        DeviceResponse device = deviceService.createDevice(request);

        ApiResponse<DeviceResponse> response = new ApiResponse<>(device,
                "Device created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
