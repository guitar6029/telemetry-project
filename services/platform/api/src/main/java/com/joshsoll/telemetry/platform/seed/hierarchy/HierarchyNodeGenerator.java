package com.joshsoll.telemetry.platform.seed.hierarchy;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.hierarchy.repository.HierarchyNodeRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

@Component
public class HierarchyNodeGenerator {

    private final HierarchyNodeRepository hierarchyNodeRepository;

    public HierarchyNodeGenerator(
            HierarchyNodeRepository hierarchyNodeRepository) {
        this.hierarchyNodeRepository = hierarchyNodeRepository;
    }

    public HierarchyNode generate(
            String name,
            Organization organization,
            HierarchyNode parentNode) {

        Instant now = Instant.now();

        HierarchyNode hierarchyNode = new HierarchyNode(
                name,
                organization,
                parentNode,
                now,
                now);

        return hierarchyNodeRepository.save(hierarchyNode);
    }

    public void generate(
            int count,
            Organization organization) {

        generate(count, organization, null, null);
    }

    public void generate(
            int count,
            Organization organization,
            HierarchyNode parentNode) {

        generate(count, organization, parentNode, null);
    }

    public void generate(
            int count,
            Organization organization,
            HierarchyNode parentNode,
            List<String> names) {

        for (int i = 1; i <= count; i++) {

            String name = names != null && i <= names.size()
                    ? names.get(i - 1)
                    : "Hierarchy Node " + i;

            generate(
                    name,
                    organization,
                    parentNode);
        }
    }
}
