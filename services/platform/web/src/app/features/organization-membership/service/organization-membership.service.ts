import { inject, Injectable } from "@angular/core";
import { ApiConstants } from "../../../constants/api.constants";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { PagedApiResponse } from "../../../common/dto/paged-api-response.dto";
import { OrganizationMembershipResponse } from "../dto/organization-membership-response.dto";


@Injectable({
    providedIn: 'root'
})

export class OrganizationMembershipService {
    private readonly TEST_ORG_ID = "30925faf-5cc1-4b0f-976f-b4f5a09a10db";
    private readonly organizationMembershipUrl =
        `${ApiConstants.API_V1}/organizations`;

    private readonly DEFAULT_PAGE = 0;
    private readonly DEFAULT_PAGE_SIZE = 10;

    private http = inject(HttpClient)


    getOrganizationMemberships(
        organizationId = this.TEST_ORG_ID,
        page = this.DEFAULT_PAGE,
        size = this.DEFAULT_PAGE_SIZE
    ): Observable<PagedApiResponse<OrganizationMembershipResponse>> {

        return this.http.get<PagedApiResponse<OrganizationMembershipResponse>>(
            `${this.organizationMembershipUrl}/${organizationId}/memberships`,
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
