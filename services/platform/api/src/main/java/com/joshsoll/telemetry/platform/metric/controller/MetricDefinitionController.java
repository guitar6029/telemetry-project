package com.joshsoll.telemetry.platform.metric.controller;

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
import com.joshsoll.telemetry.platform.metric.dto.CreateMetricDefinitionRequest;
import com.joshsoll.telemetry.platform.metric.dto.MetricDefinitionResponse;
import com.joshsoll.telemetry.platform.metric.service.MetricDefinitionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/metrics")
public class MetricDefinitionController {
    private final MetricDefinitionService metricDefinitionService;

    public MetricDefinitionController(MetricDefinitionService metricDefinitionService) {
        this.metricDefinitionService = metricDefinitionService;
    }

    @GetMapping
    public ResponseEntity<PagedApiResponse<MetricDefinitionResponse>> getMetricDefinitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedApiResponse<MetricDefinitionResponse> metricDefinitionResponses = metricDefinitionService
                .getMetricDefinitions(page, size);

        return ResponseEntity.ok(metricDefinitionResponses);

    }

    @GetMapping("/{metricDefinitionId}")
    public ResponseEntity<ApiResponse<MetricDefinitionResponse>> getMetricDefinitionById(
            @PathVariable UUID metricDefinitionId) {
        MetricDefinitionResponse metricDefinitionResponse = metricDefinitionService
                .getMetricDefinitionById(metricDefinitionId);

        ApiResponse<MetricDefinitionResponse> response = new ApiResponse<>(metricDefinitionResponse,
                "");

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MetricDefinitionResponse>> createMetricDefinition(
            @Valid @RequestBody CreateMetricDefinitionRequest request) {
        MetricDefinitionResponse metricDefinitionResponse = metricDefinitionService.createMetricDefinition(request);

        ApiResponse<MetricDefinitionResponse> response = new ApiResponse<>(metricDefinitionResponse,
                "Metric definition created successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
