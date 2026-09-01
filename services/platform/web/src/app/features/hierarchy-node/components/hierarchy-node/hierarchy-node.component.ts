import { Component, input, output } from "@angular/core";
import { HierarchyNode } from "../../../device/import/steps/hierarchy-node-selection/types/hierarchy-node.types";
import { ChevronDownIconComponent } from "../../../../components/icon/svg/chevron-down.component";
import { ChevronUpIconComponent } from "../../../../components/icon/svg/chevron-up.component";
import { SKELETON_LOADING_TREE_NODE_CHILDREN_AMOUNT } from "../../constants/hierarchy-node.constants";
import { NodeTreeSkeletonComponent } from "../../../../components/loading/loading-node-tree/loading-node-tree.component";
import { HierarchyNodeSelection } from "../../../device/import/dto/device-import.dto";

@Component({
    selector: 'telemetry-hierarchy-node',
    templateUrl: './hierarchy-node.component.html',
    imports: [
        ChevronDownIconComponent,
        ChevronUpIconComponent,
        NodeTreeSkeletonComponent
    ]
})
export class HierarchyNodeComponent {

    node = input.required<HierarchyNode>();

    readonly skeletonRange: number[] = Array.from(
        { length: SKELETON_LOADING_TREE_NODE_CHILDREN_AMOUNT },
        (_, i) => i + 1
    );

    selected = output<HierarchyNodeSelection>();

    childrenRequested = output<string>();

    selectNode(): void {
        this.selected.emit({
            id: this.node().id,
            name: this.node().name
        });
    }

    toggleExpanded(event: MouseEvent): void {
        event.stopPropagation();

        if (this.node().childrenLoaded) {
            this.node().expanded = !this.node().expanded;
            return;
        }

        this.childrenRequested.emit(this.node().id);
    }
}
