import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { InviteRequest } from "../dto/invite-request.dto";
import { ApiResponse } from "../../../../common/dto/api-response.dto";
import { InviteResponse } from "../dto/invite-response.dto";
import { Observable } from "rxjs";
import { ApiConstants } from "../../../../constants/api.constants";


@Injectable({
    providedIn: 'root'
})
export class InviteService {


    private readonly url =
        `${ApiConstants.API_V1}/organizations`

    // for now , until we get the user persistence
    private readonly organizationId =
        '30925faf-5cc1-4b0f-976f-b4f5a09a10db';

    constructor(private http: HttpClient) { }

    sendInvite(request: InviteRequest): Observable<ApiResponse<InviteResponse>> {
        return this.http.post<ApiResponse<InviteResponse>>(
            `${this.url}/${this.organizationId}/invitations`,
            request, {
            withCredentials: true
        }
        )
    }
}
