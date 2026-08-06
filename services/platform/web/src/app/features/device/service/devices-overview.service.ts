import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { ApiResponse } from "../../../common/dto/api-response.dto";
import { DeviceOverviewResponse } from "../dto/device-overview-response.dto";
import { Observable } from "rxjs";
import { ApiConstants } from "../../../constants/api.constants";

@Injectable({
    providedIn: 'root'
})

export class DevicesOverviewService {

    private readonly devicesOverviewUrl =
        `${ApiConstants.API_V1}/organizations`

    //organizations/{organizationId}/device-overview

    private readonly http = inject(HttpClient);

    // should i pass the org id or should i call it from the profile store ?

    // getDevicesOverview(): Observable<ApiResponse<DeviceOverviewResponse>> {
    //     return this.http.get<ApiResponse<DeviceOverviewResponse>> (
    //         `${this.devicesOverviewUrl}/${organizationId}/device-overview`,
    //         {
    //             withCredentials: true;
    //         }
    //     )
    // }
}
