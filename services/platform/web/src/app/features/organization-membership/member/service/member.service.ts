import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ApiResponse } from "../../../../common/dto/api-response.dto";
import { ApiConstants } from "../../../../constants/api.constants";
import { OrganizationMembershipResponse } from "../../dto/organization-membership-response.dto";
import { UpdateOrganizationMembershipRequest } from "../dto/update-organization-membership-request.dto";

@Injectable({
    providedIn: 'root'
})

export class MemberService {

    private http = inject(HttpClient)
    private readonly organizationId =
        '30925faf-5cc1-4b0f-976f-b4f5a09a10db';


    private readonly membershipUrl =
        `${ApiConstants.API_V1}/organizations`

    getMember(memberId: string): Observable<ApiResponse<OrganizationMembershipResponse>> {
        return this.http.get<ApiResponse<OrganizationMembershipResponse>>(
            `${this.membershipUrl}/${this.organizationId}/memberships/${memberId}`,
            {
                withCredentials: true
            }
        )
    }

    updateMember(
        memberId: string,
        request: UpdateOrganizationMembershipRequest
    ): Observable<ApiResponse<OrganizationMembershipResponse>> {

        return this.http.patch<ApiResponse<OrganizationMembershipResponse>>(
            `${this.membershipUrl}/${this.organizationId}/memberships/${memberId}`,
            request,
            {
                withCredentials: true
            }
        );
    }
}
