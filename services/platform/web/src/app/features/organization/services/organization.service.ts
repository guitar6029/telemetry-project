import { Injectable } from "@angular/core";
import { ApiConstants } from "../../../constants/api.constants";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { PagedApiResponse } from "../../../common/dto/paged-api-response";
import { OrganizationResponse } from "../dto/organization-response";
import { ApiResponse } from "../../../common/dto/api-response";

@Injectable({
    providedIn: 'root'
})

export class OrganizationService {

    private readonly organizationUrl =
        `${ApiConstants.API_V1}/organizations`;

    private readonly DEFAULT_PAGE = 0;
    private readonly DEFAULT_PAGE_SIZE = 10;

    constructor(private http: HttpClient) { }

    getOrganizations(
        page = this.DEFAULT_PAGE,
        size = this.DEFAULT_PAGE_SIZE
    ): Observable<PagedApiResponse<OrganizationResponse>> {
        return this.http.get<PagedApiResponse<OrganizationResponse>>(
            this.organizationUrl,
            {
                params: {
                    page,
                    size
                },
                withCredentials: true
            }
        )
    }

    getOrganization(
        id: string
    ): Observable<ApiResponse<OrganizationResponse>> {
        return this.http.get<ApiResponse<OrganizationResponse>>(
            `${this.organizationUrl}/${id}`,
            {
                withCredentials: true
            }
        )
    }
}
