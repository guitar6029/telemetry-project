import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ApiResponse } from "../../../../../../common/dto/api-response.dto";
import { ApiConstants } from "../../../../../../constants/api.constants";
import { OrganizationContextStore } from "../../../../../../core/stores/organization-context.store";
import { DevicesSummaryResponse } from "../dto/devices-summary-response.dto";

@Injectable({
    providedIn: 'root'
})

export class DevicesSummaryService {

    private readonly devicesSummaryUrl =
        `${ApiConstants.API_V1}/organizations`

    private readonly http = inject(HttpClient);
    private readonly organizationContext = inject(OrganizationContextStore);

    private get organizationId(): string {
        return this.organizationContext.requireCurrentOrganizationId();
    }

    getDevicesSummary(): Observable<ApiResponse<DevicesSummaryResponse>> {
        return this.http.get<ApiResponse<DevicesSummaryResponse>>(
            `${this.devicesSummaryUrl}/${this.organizationId}/devices-overview`,
            {
                withCredentials: true
            }
        )
    }
}
