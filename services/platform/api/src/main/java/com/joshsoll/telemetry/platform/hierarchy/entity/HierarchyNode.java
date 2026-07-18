package com.joshsoll.telemetry.platform.hierarchy.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.joshsoll.telemetry.platform.hierarchy.constants.HierarchyNodeConstants;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hierarchy_nodes")
public class HierarchyNode {

    @Id
    @Generated
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, length = HierarchyNodeConstants.NAME_MAX_LENGTH)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_node_id")
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
