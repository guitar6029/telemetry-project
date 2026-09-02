import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { DeviceImport } from "../dto/device-import-request.dto";
import { ApiConstants } from "../../../../constants/api.constants";
import { ApiResponse } from "../../../../common/dto/api-response.dto";
import { DeviceImportResponse } from "../dto/device-import-response.dto";
import { Observable } from "rxjs";
import { OrganizationContextStore } from "../../../../core/stores/organization-context.store";

@Injectable({
    providedIn: 'root'
})

export class DeviceImportService {

    private readonly http = inject(HttpClient);
    private readonly organizationContext = inject(OrganizationContextStore);


    private readonly deviceImportUrl =
        `${ApiConstants.API_V1}/organizations`;


    private get organizationId(): string {
        return this.organizationContext.requireCurrentOrganizationId();
    }

    importDevices(
        request: DeviceImport
    ): Observable<ApiResponse<DeviceImportResponse>> {

        const formData = new FormData();

        formData.append('file', request.file);
        formData.append('importMode', request.importMode);

        return this.http.post<ApiResponse<DeviceImportResponse>>(
            `${this.deviceImportUrl}/${this.organizationId}/${request.deviceTemplateId}/${request.hierarchyNodeId}/import`,
            formData,
            {
                withCredentials: true
            }
        );
    }
}
