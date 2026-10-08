import { Component, inject, OnInit, signal } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { ButtonComponent } from "../../../../components/button/button.component";
import { ErrorComponent } from "../../../../components/error/error.component";
import { LoadingSpinnerComponent } from "../../../../components/loading/loading-spinner/loading-spinner.component";
import { PageComponent } from "../../../../components/page/page.component";
import { DeviceImportResponse } from "../dto/device-import-response.dto";
import { DeviceImportHistoryService } from "../service/device-import-history.service";

@Component({
    selector: 'telemetry-device-import-detail',
    imports: [PageComponent, ButtonComponent, ErrorComponent, LoadingSpinnerComponent],
    templateUrl: './device-import-detail.component.html'
})
export class DeviceImportDetailComponent implements OnInit {

    readonly deviceImport = signal<DeviceImportResponse | null>(null);
    readonly loading = signal(true);
    readonly error = signal<string | null>(null);

    private readonly route = inject(ActivatedRoute);
    private readonly deviceImportHistoryService = inject(DeviceImportHistoryService);

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

        this.deviceImportHistoryService.getDeviceImport(importId).subscribe({
            next: (response) => {
                this.deviceImport.set(response.data);
                this.loading.set(false);
            },
            error: (httpError) => {
                this.error.set(httpError.status === 404
                    ? "Device import not found."
                    : "Unable to load device import.");
                this.loading.set(false);
            }
        });
    }
}
