import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { PagedApiResponse } from "../../../../common/dto/paged-api-response.dto";
import { ApiResponse } from "../../../../common/dto/api-response.dto";
import { ApiConstants } from "../../../../constants/api.constants";
import { OrganizationContextStore } from "../../../../core/stores/organization-context.store";
import { DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from "../../../../components/pagination/constants/pagination.constants";
import { DeviceImportResponse } from "../dto/device-import-response.dto";

@Injectable({
    providedIn: 'root'
})
export class DeviceImportHistoryService {

    private readonly http = inject(HttpClient);
    private readonly organizationContext = inject(OrganizationContextStore);
    private readonly importsUrl = `${ApiConstants.API_V1}/organizations`;

    private get organizationId(): string {
        return this.organizationContext.requireCurrentOrganizationId();
    }

    getDeviceImports(
        page = DEFAULT_PAGE,
        size = DEFAULT_PAGE_SIZE
    ): Observable<PagedApiResponse<DeviceImportResponse>> {
        return this.http.get<PagedApiResponse<DeviceImportResponse>>(
            `${this.importsUrl}/${this.organizationId}/imports`,
            {
                params: { page, size },
                withCredentials: true
            }
        );
    }

    getDeviceImport(importId: string): Observable<ApiResponse<DeviceImportResponse>> {
        return this.http.get<ApiResponse<DeviceImportResponse>>(
            `${this.importsUrl}/${this.organizationId}/imports/${importId}`,
            {
                withCredentials: true
            }
        );
    }
}
