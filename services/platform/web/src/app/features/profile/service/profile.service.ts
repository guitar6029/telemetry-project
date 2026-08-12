import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { ApiConstants } from "../../../constants/api.constants";
import { Observable } from "rxjs";
import { ApiResponse } from "../../../common/dto/api-response.dto";
import { MeResponse } from "../dto/me-response.dto";
import { UpdateLastOrganizationUsed } from "../../../dto/profile-last-organization-used.dto";


@Injectable({
    providedIn: 'root'
})

export class ProfileService {

    private readonly url =
        `${ApiConstants.API_V1}/profile`;

    private readonly http = inject(HttpClient)

    me(): Observable<ApiResponse<MeResponse>> {
        return this.http.get<ApiResponse<MeResponse>>(
            `${this.url}/me`,
            {
                withCredentials: true
            }
        )
    }

    updateLastOrganizationUsed(request: UpdateLastOrganizationUsed): Observable<ApiResponse<MeResponse>> {

        console.log("request : ", request)

        return this.http.patch<ApiResponse<MeResponse>>(
            `${this.url}/last-organization-used`,
            request,
            {
                withCredentials: true,

            }

        )
    }
}
