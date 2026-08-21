import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { ApiResponse } from "../../../../../../common/dto/api-response.dto";
import { ApiConstants } from "../../../../../../constants/api.constants";
import { OrganizationContextStore } from "../../../../../../core/stores/organization-context.store";
import { HierarchyNodeResponse } from "../../../../../hierarchy-node/dto/hierarchy-node-response.dto";

@Injectable({
    providedIn: 'root'
})
export class HierarchyNodeSelectionService {

    private readonly hierarchyUrl =
        `${ApiConstants.API_V1}/hierarchy`;

    private readonly http = inject(HttpClient);
    private readonly organizationContext =
        inject(OrganizationContextStore);

    private get organizationId(): string {
        return this.organizationContext.requireCurrentOrganizationId();
    }

    getHierarchy(): Observable<ApiResponse<HierarchyNodeResponse[]>> {
        const params = new HttpParams()
            .set('organizationId', this.organizationId);

        return this.http.get<ApiResponse<HierarchyNodeResponse[]>>(
            this.hierarchyUrl,
            {
                params,
                withCredentials: true
            }
        );
    }

    getChildren(
        nodeId: string
    ): Observable<ApiResponse<HierarchyNodeResponse[]>> {

        return this.http.get<ApiResponse<HierarchyNodeResponse[]>>(
            `${this.hierarchyUrl}/${nodeId}/children`,
            {
                withCredentials: true
            }
        );
    }
}
