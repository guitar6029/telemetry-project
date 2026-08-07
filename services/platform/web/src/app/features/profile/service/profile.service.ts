import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { ApiConstants } from "../../../constants/api.constants";
import { Observable } from "rxjs";
import { ApiResponse } from "../../../common/dto/api-response.dto";
import { MeResponse } from "../dto/me-response.dto";


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

    updateLastOrganizationUsed(lastOrganizationUsed: string): Observable<ApiResponse<MeResponse>> {
        return this.http.patch<ApiResponse<MeResponse>>(
            `${this.url}//last-organization-used`,
            lastOrganizationUsed,
            {
                withCredentials: true
            }

        )
    }
}
