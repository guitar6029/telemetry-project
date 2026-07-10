package com.joshsoll.telemetry.platform.hierarchy.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshsoll.telemetry.platform.common.response.ApiResponse;
import com.joshsoll.telemetry.platform.hierarchy.dto.CreateHierarchyNodeRequest;
import com.joshsoll.telemetry.platform.hierarchy.dto.HierarchyNodeResponse;
import com.joshsoll.telemetry.platform.hierarchy.service.HierarchyNodeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/hierarchy")
public class HierarchyNodeController {

    private final HierarchyNodeService hierarchyNodeService;

    public HierarchyNodeController(HierarchyNodeService hierarchyNodeService) {
        this.hierarchyNodeService = hierarchyNodeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HierarchyNodeResponse>> createHierarchyNode(
            @Valid @RequestBody CreateHierarchyNodeRequest request) {
        HierarchyNodeResponse node = hierarchyNodeService.createHierarchyNode(request);
        ApiResponse<HierarchyNodeResponse> response = new ApiResponse<>(node, "Hierarchy node added successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HierarchyNodeResponse>> getHierarchyNodeById(@PathVariable UUID id) {
        HierarchyNodeResponse node = hierarchyNodeService.getHierarchyNodeById(id);
        ApiResponse<HierarchyNodeResponse> response = new ApiResponse<>(node, "");
        return ResponseEntity.ok(response);
    }
}
