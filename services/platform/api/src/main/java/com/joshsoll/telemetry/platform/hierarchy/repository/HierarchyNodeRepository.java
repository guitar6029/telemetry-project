package com.joshsoll.telemetry.platform.hierarchy.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

public interface HierarchyNodeRepository extends JpaRepository<HierarchyNode, UUID> {

    boolean existsByOrganizationAndParentNodeAndName(Organization organization, HierarchyNode parentNode,
            String nodeName);

    List<HierarchyNode> findAllByOrganization(Organization organization);
}
