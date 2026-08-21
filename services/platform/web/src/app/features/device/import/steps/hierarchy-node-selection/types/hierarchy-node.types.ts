import { HierarchyNodeResponse } from "../../../../../hierarchy-node/dto/hierarchy-node-response.dto";

export interface HierarchyNode extends HierarchyNodeResponse {
    children: HierarchyNode[];
    childrenLoaded: boolean;
    expanded: boolean;
    loadingChildren: boolean;
}
