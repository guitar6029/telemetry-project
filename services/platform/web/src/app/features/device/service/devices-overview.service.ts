import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { ApiResponse } from "../../../common/dto/api-response.dto";
import { DeviceOverviewResponse } from "../dto/device-overview-response.dto";
import { Observable } from "rxjs";
import { ApiConstants } from "../../../constants/api.constants";
import { OrganizationContextStore } from "../../../core/stores/organization-context.store";

@Injectable({
    providedIn: 'root'
})

export class DevicesOverviewService {

    private readonly devicesOverviewUrl =
        `${ApiConstants.API_V1}/organizations`

    private readonly http = inject(HttpClient);
    private readonly organizationContext = inject(OrganizationContextStore);

    private get organizationId(): string {
        return this.organizationContext.requireCurrentOrganizationId();
    }

    getDevicesOverview(): Observable<ApiResponse<DeviceOverviewResponse>> {

        console.log(` url : ${this.devicesOverviewUrl}/${this.organizationId}/device-overview`,)
        return this.http.get<ApiResponse<DeviceOverviewResponse>>(
            `${this.devicesOverviewUrl}/${this.organizationId}/device-overview`,
            {
                withCredentials: true
            }
        )
    }
}
