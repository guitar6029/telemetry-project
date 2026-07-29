import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { InviteRequest } from "../dto/invite.request";
import { ApiResponse } from "../../../../common/dto/api-response";
import { InviteResponse } from "../dto/invite.response";
import { Observable } from "rxjs";
import { ApiConstants } from "../../../../constants/api.constants";


@Injectable({
    providedIn: 'root'
})
export class InviteService {


    private readonly url =
        `${ApiConstants.API_V1}/organizations`

    private readonly organizationId =
        '47dbd192-2342-4d50-ae0d-3407db4f274b';

    constructor(private http: HttpClient) { }

    sendInvite(request: InviteRequest): Observable<ApiResponse<InviteResponse>> {
        return this.http.post<ApiResponse<InviteResponse>>(
            `${this.url}/${this.organizationId}/memberships/invite`,
            request, {
            withCredentials: true
        }
        )
    }
}
