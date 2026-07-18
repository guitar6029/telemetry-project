package com.joshsoll.telemetry.platform.hierarchy.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.hierarchy.dto.CreateHierarchyNodeRequest;
import com.joshsoll.telemetry.platform.hierarchy.dto.HierarchyNodeResponse;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.hierarchy.exception.HierarchyNodeNotFoundException;
import com.joshsoll.telemetry.platform.hierarchy.repository.HierarchyNodeRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.exception.OrganizationNotFoundException;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

@Service
public class HierarchyNodeService {
    private final HierarchyNodeRepository hierarchyNodeRepository;
    private final OrganizationRepository organizationRepository;

    public HierarchyNodeService(HierarchyNodeRepository hierarchyNodeRepository,
            OrganizationRepository organizationRepository) {
        this.hierarchyNodeRepository = hierarchyNodeRepository;
        this.organizationRepository = organizationRepository;
    }

    public HierarchyNodeResponse createHierarchyNode(CreateHierarchyNodeRequest request) {
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new OrganizationNotFoundException(request.getOrganizationId()));
        UUID parentId = request.getParentNodeId();
        HierarchyNode parentNode = null;
        if (parentId != null) {
            parentNode = hierarchyNodeRepository.findById(parentId)
                    .orElseThrow(() -> new HierarchyNodeNotFoundException(parentId));
        }

        if (parentNode != null && !parentNode.getOrganization().equals(organization)) {
            throw new IllegalArgumentException(
                    "Parent node does not belong to this organization");
        }

        // if we have parentId, parentNode , and name
        // then we check the next step
        // duplicate check - org + parentNode + req.name

        // throw illegal argument exception for now
        // if exists
        if (hierarchyNodeRepository.existsByOrganizationAndParentNodeAndName(organization, parentNode,
                request.getName())) {
            throw new IllegalArgumentException("Node already exists");
        }

        Instant now = Instant.now();

        HierarchyNode node = new HierarchyNode(request.getName(), organization, parentNode, now, now);

        // save the request
        HierarchyNode savedNode = hierarchyNodeRepository.save(node);

        return toResponse(savedNode);

    }

    public HierarchyNodeResponse getHierarchyNodeById(UUID id) {
        HierarchyNode node = hierarchyNodeRepository.findById(id)
                .orElseThrow(() -> new HierarchyNodeNotFoundException(id));
        return toResponse(node);
    }

    // public HierarchyNodeResponse getHierarchyNodes() {

    // }

    private boolean hasChildren(HierarchyNode node) {
        return hierarchyNodeRepository.existsByParentNode(node);
    }

    private HierarchyNodeResponse toResponse(HierarchyNode node) {
        return new HierarchyNodeResponse(
                node.getName(),
                node.getId(),
                node.getParentNodeId(),
                node.getOrganization().getId(),
                hasChildren(node));
    }

    private List<HierarchyNodeResponse> toResponseList(List<HierarchyNode> nodes) {
        // convert the list of hierarchy nodes to list of hierarchy node response
        List<HierarchyNodeResponse> nodesResponses = new ArrayList<>();

        // iterate over the nodes and use the helper to convert to the new NodeResponse
        for (HierarchyNode node : nodes) {
            nodesResponses.add(toResponse(node));
        }

        return nodesResponses;
    }

    public List<HierarchyNodeResponse> getHierarchyByOrganization(UUID organizationId) {

        // check if organization exists
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        // retrive the list of nodes
        List<HierarchyNode> nodes = hierarchyNodeRepository.findAllByOrganization(organization);

        return toResponseList(nodes);
    }

    public List<HierarchyNodeResponse> getChildNodesByParentNodeId(UUID nodeId) {

        // check if node exists
        HierarchyNode parentNode = hierarchyNodeRepository.findById(nodeId)
                .orElseThrow(() -> new HierarchyNodeNotFoundException(nodeId));

        // retrive the list of child nodes from the parentNode
        List<HierarchyNode> nodes = hierarchyNodeRepository.findAllByParentNode(parentNode);

        return toResponseList(nodes);
    }

}
