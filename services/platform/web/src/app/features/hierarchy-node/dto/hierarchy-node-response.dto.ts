export interface HierarchyNodeResponse {
    name: string;
    id: string;
    parentNodeId: string | null;
    organizationId: string;
    hasChildren: boolean;
}
