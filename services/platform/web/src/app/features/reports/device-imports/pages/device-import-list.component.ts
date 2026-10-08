import { Component, inject, OnInit, signal } from "@angular/core";
import { DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from "../../../../components/pagination/constants/pagination.constants";
import { EmptyStateComponent } from "../../../../common/components/empty-state/empty-state.component";
import { ErrorComponent } from "../../../../components/error/error.component";
import { LoadingSpinnerComponent } from "../../../../components/loading/loading-spinner/loading-spinner.component";
import { PaginationComponent } from "../../../../components/pagination/pagination.component";
import { PaginationState } from "../../../../components/pagination/types/pagination.types";
import { PageComponent } from "../../../../components/page/page.component";
import { TableComponent } from "../../../../components/table/table.component";
import { DeviceImportColumnDefinitions } from "../columns/device-import-column-definitions";
import { DeviceImportResponse } from "../dto/device-import-response.dto";
import { DeviceImportHistoryService } from "../service/device-import-history.service";

@Component({
    selector: 'telemetry-device-import-list',
    imports: [PageComponent, TableComponent, PaginationComponent, EmptyStateComponent, ErrorComponent, LoadingSpinnerComponent],
    templateUrl: './device-import-list.component.html'
})
export class DeviceImportListComponent implements OnInit {

    protected readonly columns = DeviceImportColumnDefinitions;
    readonly imports = signal<DeviceImportResponse[]>([]);
    readonly loading = signal(true);
    readonly error = signal<string | null>(null);
    readonly pagination = signal<PaginationState>({
        page: DEFAULT_PAGE,
        size: DEFAULT_PAGE_SIZE,
        total: 0,
        totalPages: 0
    });

    private readonly deviceImportHistoryService = inject(DeviceImportHistoryService);

    ngOnInit(): void {
        this.loadImports();
    }

    loadImports(
        page = this.pagination().page,
        size = this.pagination().size
    ): void {
        this.loading.set(true);
        this.error.set(null);

        this.deviceImportHistoryService.getDeviceImports(page, size).subscribe({
            next: (response) => {
                this.imports.set(response.data);
                this.pagination.set({
                    page: response.page,
                    size: response.size,
                    total: response.total,
                    totalPages: response.totalPages
                });
                this.loading.set(false);
            },
            error: () => {
                this.error.set("Unable to load device imports.");
                this.loading.set(false);
            }
        });
    }

    protected onPageChange(page: number): void {
        this.pagination.update(state => ({ ...state, page }));
        this.loadImports();
    }

    protected onSizeChange(size: number): void {
        this.pagination.update(state => ({ ...state, size, page: DEFAULT_PAGE }));
        this.loadImports();
    }
}
