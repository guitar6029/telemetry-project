import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ApiResponse } from "../../../../common/dto/api-response.dto";
import { ApiConstants } from "../../../../constants/api.constants";
import { OrganizationMembershipResponse } from "../../dto/organization-membership-response.dto";
import { UpdateOrganizationMembershipRequest } from "../dto/update-organization-membership-request.dto";
import { OrganizationContextStore } from "../../../../core/stores/organization-context.store";

@Injectable({
    providedIn: 'root'
})

export class MemberService {

    private readonly http = inject(HttpClient)
    private readonly organizationContext = inject(OrganizationContextStore);

    private readonly membershipUrl =
        `${ApiConstants.API_V1}/organizations`;

    private get organizationId(): string {
        return this.organizationContext.requireCurrentOrganizationId();
    }

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
