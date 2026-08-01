import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { InviteRequest } from "../dto/invite-request.dto";
import { ApiResponse } from "../../../../common/dto/api-response.dto";
import { InviteResponse } from "../dto/invite-response.dto";
import { Observable } from "rxjs";
import { ApiConstants } from "../../../../constants/api.constants";
import { ProfileStore } from "../../../../core/stores/profile.store";


@Injectable({
    providedIn: 'root'
})
export class InviteService {


    private readonly url =
        `${ApiConstants.API_V1}/organizations`

    private profileStore = inject(ProfileStore)

    private readonly organizationId =
        this.profileStore.getOrganizationId

    private http = inject(HttpClient)

    sendInvite(request: InviteRequest): Observable<ApiResponse<InviteResponse>> {
        return this.http.post<ApiResponse<InviteResponse>>(
            `${this.url}/${this.organizationId}/invitations`,
            request, {
            withCredentials: true
        }
        )
    }
}
