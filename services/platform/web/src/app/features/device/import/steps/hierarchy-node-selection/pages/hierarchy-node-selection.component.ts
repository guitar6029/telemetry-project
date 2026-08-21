import { Component, inject, OnInit, output, signal } from "@angular/core";

import { HierarchyNodeSelectionService } from "../service/hierarchy-node-selection.service";
import { HierarchyNodeResponse } from "../../../../../hierarchy-node/dto/hierarchy-node-response.dto";
import { HierarchyNode } from "../types/hierarchy-node.types";
import { HierarchyNodeComponent } from "../../../../../hierarchy-node/components/hierarchy-node/hierarchy-node.component";

@Component({
    selector: 'telemetry-hierarchy-node-selection',
    templateUrl: './hierarchy-node-selection.component.html',
    imports: [HierarchyNodeComponent]
})
export class HierarchyNodeSelectionComponent implements OnInit {

    private readonly hierarchyNodeService =
        inject(HierarchyNodeSelectionService);

    hierarchyNodes = signal<HierarchyNode[]>([]);

    selectedHierarchyNode = output<string>();

    ngOnInit(): void {
        this.getHierarchyNode();
    }

    getHierarchyNode(): void {
        this.hierarchyNodeService.getHierarchy().subscribe({
            next: (response) => {
                this.hierarchyNodes.set(
                    response.data.map(node => this.toHierarchyNode(node))
                );
            },
            error: (httpError) => {
                console.error(httpError);
            }
        });
    }

    getHierarchyChildrenNode(hierarchyNodeId: string): void {

        const node = this.findNode(
            this.hierarchyNodes(),
            hierarchyNodeId
        );

        if (!node || node.childrenLoaded || node.loadingChildren) {
            return;
        }

        this.hierarchyNodes.update(nodes =>
            this.updateNode(nodes, hierarchyNodeId, node => ({
                ...node,
                loadingChildren: true
            }))
        );

        this.hierarchyNodeService.getChildren(hierarchyNodeId).subscribe({
            next: (response) => {

                this.hierarchyNodes.update(nodes =>
                    this.updateNode(nodes, hierarchyNodeId, node => ({
                        ...node,
                        children: response.data.map(child =>
                            this.toHierarchyNode(child)
                        ),
                        childrenLoaded: true,
                        loadingChildren: false,
                        expanded: true
                    }))
                );
            },

            error: (httpError) => {

                this.hierarchyNodes.update(nodes =>
                    this.updateNode(nodes, hierarchyNodeId, node => ({
                        ...node,
                        loadingChildren: false
                    }))
                );

                console.error(httpError);
            }
        });
    }

    selectNode(nodeId: string): void {
        this.selectedHierarchyNode.emit(nodeId);
    }

    private toHierarchyNode(
        node: HierarchyNodeResponse
    ): HierarchyNode {
        return {
            ...node,
            children: [],
            childrenLoaded: false,
            expanded: false,
            loadingChildren: false
        };
    }

    private findNode(
        nodes: HierarchyNode[],
        nodeId: string
    ): HierarchyNode | undefined {

        for (const node of nodes) {

            if (node.id === nodeId) {
                return node;
            }

            const found = this.findNode(node.children, nodeId);

            if (found) {
                return found;
            }
        }

        return undefined;
    }


    private updateNode(
        nodes: HierarchyNode[],
        nodeId: string,
        update: (node: HierarchyNode) => HierarchyNode
    ): HierarchyNode[] {

        return nodes.map(node => {

            if (node.id === nodeId) {
                return update(node);
            }

            if (node.children.length > 0) {
                return {
                    ...node,
                    children: this.updateNode(
                        node.children,
                        nodeId,
                        update
                    )
                };
            }

            return node;
        });
    }
}
