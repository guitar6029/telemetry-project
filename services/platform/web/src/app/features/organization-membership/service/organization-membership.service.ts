import { inject, Injectable } from "@angular/core";
import { ApiConstants } from "../../../constants/api.constants";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { PagedApiResponse } from "../../../common/dto/paged-api-response.dto";
import { OrganizationMembershipResponse } from "../dto/organization-membership-response.dto";
import { OrganizationContextStore } from "../../../core/stores/organization-context.store";


@Injectable({
    providedIn: 'root'
})

export class OrganizationMembershipService {
    private readonly organizationMembershipUrl =
        `${ApiConstants.API_V1}/organizations`;

    private readonly DEFAULT_PAGE = 0;
    private readonly DEFAULT_PAGE_SIZE = 10;

    private readonly http = inject(HttpClient)
    private readonly organizationContext = inject(OrganizationContextStore);

    private get organizationId(): string {
        return this.organizationContext.requireCurrentOrganizationId();
    }


    getOrganizationMemberships(
        page = this.DEFAULT_PAGE,
        size = this.DEFAULT_PAGE_SIZE
    ): Observable<PagedApiResponse<OrganizationMembershipResponse>> {
        return this.http.get<PagedApiResponse<OrganizationMembershipResponse>>(
            `${this.organizationMembershipUrl}/${this.organizationId}/memberships`,
            {
                params: {
                    page,
                    size
                },
                withCredentials: true
            }
        );
    }

}
