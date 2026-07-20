package com.joshsoll.telemetry.platform.hierarchy.controller;

import java.util.List;
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
import com.joshsoll.telemetry.platform.common.response.ResponseFactory;
import com.joshsoll.telemetry.platform.hierarchy.dto.CreateHierarchyNodeRequest;
import com.joshsoll.telemetry.platform.hierarchy.dto.HierarchyNodeResponse;
import com.joshsoll.telemetry.platform.hierarchy.service.HierarchyNodeService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/hierarchy")
public class HierarchyNodeController {

    private final HierarchyNodeService hierarchyNodeService;
    private final String DOMAIN_NAME = "Hierarchy Node";

    public HierarchyNodeController(HierarchyNodeService hierarchyNodeService) {
        this.hierarchyNodeService = hierarchyNodeService;

    }

    @PostMapping
    public ResponseEntity<ApiResponse<HierarchyNodeResponse>> createHierarchyNode(
            @Valid @RequestBody CreateHierarchyNodeRequest request) {
        HierarchyNodeResponse node = hierarchyNodeService.createHierarchyNode(request);
        return ResponseFactory.created(node, DOMAIN_NAME);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HierarchyNodeResponse>> getHierarchyNodeById(@PathVariable UUID id) {
        HierarchyNodeResponse node = hierarchyNodeService.getHierarchyNodeById(id);
        return ResponseFactory.ok(node, DOMAIN_NAME);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HierarchyNodeResponse>>> getHierarchyByOrganization(
            @RequestParam UUID organizationId) {

        List<HierarchyNodeResponse> nodes = hierarchyNodeService.getHierarchyByOrganization(organizationId);
        return ResponseFactory.ok(nodes, null);

    }

    @GetMapping("/{nodeId}/children")
    public ResponseEntity<ApiResponse<List<HierarchyNodeResponse>>> getHierarchyChildNodesByparent(
            @PathVariable UUID nodeId) {
        List<HierarchyNodeResponse> nodes = hierarchyNodeService.getChildNodesByParentNodeId(nodeId);
        return ResponseFactory.ok(nodes, null);
    }
}
