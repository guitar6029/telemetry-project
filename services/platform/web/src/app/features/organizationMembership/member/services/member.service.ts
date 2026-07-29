import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ApiResponse } from "../../../../common/dto/api-response";
import { ApiConstants } from "../../../../constants/api.constants";
import { OrganizationMembershipResponse } from "../../dto/organization-membership-response";

@Injectable({
    providedIn: 'root'
})

export class MemberService {

    constructor(private http: HttpClient) { }
    private readonly organizationId =
        '47dbd192-2342-4d50-ae0d-3407db4f274b';


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
}
