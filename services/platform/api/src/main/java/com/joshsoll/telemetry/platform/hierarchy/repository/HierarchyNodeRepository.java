package com.joshsoll.telemetry.platform.hierarchy.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;

public interface HierarchyNodeRepository extends JpaRepository<HierarchyNode, UUID> {

}
