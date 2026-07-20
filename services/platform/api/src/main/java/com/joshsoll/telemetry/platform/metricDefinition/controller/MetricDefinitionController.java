package com.joshsoll.telemetry.platform.metricDefinition.controller;

import java.util.UUID;

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
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.metricDefinition.dto.CreateMetricDefinitionRequest;
import com.joshsoll.telemetry.platform.metricDefinition.dto.MetricDefinitionResponse;
import com.joshsoll.telemetry.platform.metricDefinition.service.MetricDefinitionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/metric-definitions")
public class MetricDefinitionController {
        private final MetricDefinitionService metricDefinitionService;
        private final String DOMAIN_NAME = "Metric Definition";

        public MetricDefinitionController(MetricDefinitionService metricDefinitionService) {
                this.metricDefinitionService = metricDefinitionService;
        }

        @GetMapping
        public ResponseEntity<PagedApiResponse<MetricDefinitionResponse>> getMetricDefinitions(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                PagedApiResponse<MetricDefinitionResponse> metricDefinitionResponses = metricDefinitionService
                                .getMetricDefinitions(page, size);

                return ResponseFactory.ok(metricDefinitionResponses);

        }

        @GetMapping("/{metricDefinitionId}")
        public ResponseEntity<ApiResponse<MetricDefinitionResponse>> getMetricDefinitionById(
                        @PathVariable UUID metricDefinitionId) {
                MetricDefinitionResponse metricDefinitionResponse = metricDefinitionService
                                .getMetricDefinitionById(metricDefinitionId);

                return ResponseFactory.ok(metricDefinitionResponse, null);
        }

        @PostMapping
        public ResponseEntity<ApiResponse<MetricDefinitionResponse>> createMetricDefinition(
                        @Valid @RequestBody CreateMetricDefinitionRequest request) {
                MetricDefinitionResponse metricDefinitionResponse = metricDefinitionService
                                .createMetricDefinition(request);
                return ResponseFactory.created(metricDefinitionResponse, DOMAIN_NAME);
        }

}
