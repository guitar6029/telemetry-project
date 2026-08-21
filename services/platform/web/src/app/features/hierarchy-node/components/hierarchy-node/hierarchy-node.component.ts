import { Component, input, output, signal } from "@angular/core";
import { HierarchyNode } from "../../../device/import/steps/hierarchy-node-selection/types/hierarchy-node.types";

@Component({
    selector: 'telemetry-hierarchy-node',
    templateUrl: './hierarchy-node.component.html'
})
export class HierarchyNodeComponent {

    node = input.required<HierarchyNode>();

    selected = output<string>();

    childrenRequested = output<string>();

    expanded = signal(false);

    selectNode(): void {
        this.selected.emit(this.node().id);
    }

    toggleExpanded(event: MouseEvent): void {
        event.stopPropagation();

        if (!this.expanded()) {
            this.childrenRequested.emit(this.node().id);
        }

        this.expanded.update(value => !value);
    }
}
