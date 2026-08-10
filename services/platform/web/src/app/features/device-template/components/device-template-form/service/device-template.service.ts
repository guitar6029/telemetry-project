import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { DeviceTemplateRequest } from "../../../dto/device-template-request.dto";
import { Observable } from "rxjs";
import { ApiResponse } from "../../../../../common/dto/api-response.dto";
import { DeviceTemplateResponse } from "../../../dto/device-template-response.dto";
import { OrganizationContextStore } from "../../../../../core/stores/organization-context.store";
import { ApiConstants } from "../../../../../constants/api.constants";
import { DeviceTemplateUpdateRequest } from "../../../dto/device-template-update-request.dto";

@Injectable({
    providedIn: 'root'
})

export class DeviceTemplateService {

    private readonly http = inject(HttpClient);

    private readonly organizationContext = inject(OrganizationContextStore);

    private readonly deviceTemplateUrl =
        `${ApiConstants.API_V1}/organizations`;

    private get organizationId(): string {
        return this.organizationContext.requireCurrentOrganizationId();
    }

    createDeviceTemplate(request: DeviceTemplateRequest): Observable<ApiResponse<DeviceTemplateResponse>> {
        return this.http.post<ApiResponse<DeviceTemplateResponse>>(
            `${this.deviceTemplateUrl}/${this.organizationId}/device-templates`,
            request,
            {
                withCredentials: true
            }
        )
    }

    getDeviceTemplate(deviceTemplateId: string): Observable<ApiResponse<DeviceTemplateResponse>> {
        return this.http.get<ApiResponse<DeviceTemplateResponse>>(
            `${this.deviceTemplateUrl}/${this.organizationId}/device-templates/${deviceTemplateId}`,
            {
                withCredentials: true
            }
        )
    }

    updateDeviceTemplate(request: DeviceTemplateUpdateRequest, deviceTemplateId: string): Observable<ApiResponse<DeviceTemplateResponse>> {
        return this.http.put<ApiResponse<DeviceTemplateResponse>>(
            `${this.deviceTemplateUrl}/${this.organizationId}/device-templates/${deviceTemplateId}`,
            request,
            {
                withCredentials: true
            }
        )
    }
}
