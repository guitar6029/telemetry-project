package com.joshsoll.telemetry.platform.hierarchy.entity;

import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.organization.entity.Organization;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hierarchy_nodes")
public class HierarchyNode {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private HierarchyNode parentNode;

    private Instant createdAt;
    private Instant updatedAt;

    protected HierarchyNode() {
    }

    public HierarchyNode(String name, Organization organization, HierarchyNode parentNode, Instant createdAt,
            Instant updatedAt) {
        this.name = name;
        this.organization = organization;
        this.parentNode = parentNode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getParentNodeId() {
        if (parentNode == null) {
            return null;
        }
        return parentNode.getId();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Organization getOrganization() {
        return organization;
    }

}
