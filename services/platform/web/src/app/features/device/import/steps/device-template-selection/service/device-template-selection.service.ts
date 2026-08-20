import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { ApiConstants } from "../../../../../../constants/api.constants";
import { OrganizationContextStore } from "../../../../../../core/stores/organization-context.store";
import { Observable } from "rxjs";
import { DeviceTemplateResponse } from "../../../../../device-template/dto/device-template-response.dto";
import { ApiResponse } from "../../../../../../common/dto/api-response.dto";
import { DeviceTemplateOptionResponse } from "../../../../../device-template/dto/device-template-option-response.dto";

@Injectable({
    providedIn: 'root'
})

export class DeviceTemplateSelectionService {
    private readonly deviceTemplateUrl =
        `${ApiConstants.API_V1}/organizations`

    private readonly http = inject(HttpClient);
    private readonly organizationContext = inject(OrganizationContextStore);

    private get organizationId(): string {
        return this.organizationContext.requireCurrentOrganizationId();
    }

    getDeviceTemplatesBySearchQuery(query: string): Observable<ApiResponse<DeviceTemplateOptionResponse[]>> {

        const params = new HttpParams()
            .set('query', query);

        return this.http.get<ApiResponse<DeviceTemplateOptionResponse[]>>(
            `${this.deviceTemplateUrl}/${this.organizationId}/device-templates/search`,
            {
                params,
                withCredentials: true
            }
        );
    }
}
