import { Component, inject, OnInit, signal } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { ButtonComponent } from "../../../../components/button/button.component";
import { ErrorComponent } from "../../../../components/error/error.component";
import { LoadingSpinnerComponent } from "../../../../components/loading/loading-spinner/loading-spinner.component";
import { DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from "../../../../components/pagination/constants/pagination.constants";
import { PaginationComponent } from "../../../../components/pagination/pagination.component";
import { PaginationState } from "../../../../components/pagination/types/pagination.types";
import { PageComponent } from "../../../../components/page/page.component";
import { EmptyStateComponent } from "../../../../common/components/empty-state/empty-state.component";
import { TableComponent } from "../../../../components/table/table.component";
import { DeviceImportErrorColumnDefinitions } from "../columns/device-import-error-column-definitions";
import { DeviceImportErrorResponse } from "../dto/device-import-error-response.dto";
import { DeviceImportResponse } from "../dto/device-import-response.dto";
import { DeviceImportHistoryService } from "../service/device-import-history.service";

@Component({
    selector: 'telemetry-device-import-detail',
    imports: [PageComponent, ButtonComponent, ErrorComponent, LoadingSpinnerComponent, TableComponent, PaginationComponent, EmptyStateComponent],
    templateUrl: './device-import-detail.component.html'
})
export class DeviceImportDetailComponent implements OnInit {

    readonly deviceImport = signal<DeviceImportResponse | null>(null);
    readonly loading = signal(true);
    readonly error = signal<string | null>(null);
    protected readonly errorColumns = DeviceImportErrorColumnDefinitions;
    readonly importErrors = signal<DeviceImportErrorResponse[]>([]);
    readonly errorsLoading = signal(false);
    readonly errorsError = signal<string | null>(null);
    readonly errorsPagination = signal<PaginationState>({
        page: DEFAULT_PAGE,
        size: DEFAULT_PAGE_SIZE,
        total: 0,
        totalPages: 0
    });

    private readonly route = inject(ActivatedRoute);
    private readonly deviceImportHistoryService = inject(DeviceImportHistoryService);
    private importId: string | null = null;

    protected formatDateTime(value: string | null): string {
        if (!value) {
            return '—';
        }

        const date = new Date(value);
        return Number.isNaN(date.getTime()) ? value : date.toLocaleString();
    }

    ngOnInit(): void {
        const importId = this.route.snapshot.paramMap.get('importId');
        if (!importId) {
            this.error.set("Invalid device import ID.");
            this.loading.set(false);
            return;
        }
        this.importId = importId;

        this.deviceImportHistoryService.getDeviceImport(importId).subscribe({
            next: (response) => {
                this.deviceImport.set(response.data);
                this.loading.set(false);
                this.loadImportErrors();
            },
            error: (httpError) => {
                this.error.set(httpError.status === 404
                    ? "Device import not found."
                    : "Unable to load device import.");
                this.loading.set(false);
            }
        });
    }

    protected onErrorsPageChange(page: number): void {
        this.errorsPagination.update(state => ({ ...state, page }));
        this.loadImportErrors();
    }

    protected onErrorsSizeChange(size: number): void {
        this.errorsPagination.update(state => ({ ...state, size, page: DEFAULT_PAGE }));
        this.loadImportErrors();
    }

    private loadImportErrors(): void {
        if (!this.importId) {
            return;
        }

        const { page, size } = this.errorsPagination();
        this.errorsLoading.set(true);
        this.errorsError.set(null);

        this.deviceImportHistoryService.getDeviceImportErrors(this.importId, page, size).subscribe({
            next: (response) => {
                this.importErrors.set(response.data);
                this.errorsPagination.set({
                    page: response.page,
                    size: response.size,
                    total: response.total,
                    totalPages: response.totalPages
                });
                this.errorsLoading.set(false);
            },
            error: () => {
                this.errorsError.set("Unable to load device import errors.");
                this.errorsLoading.set(false);
            }
        });
    }
}
