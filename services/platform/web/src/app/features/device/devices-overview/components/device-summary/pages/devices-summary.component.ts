import { Component, inject, OnInit, signal } from "@angular/core";

import { NotificationService } from "../../../../../../common/notification/service/notification.service";
import { MessageDefaultConstants } from "../../../../../../constants/message.constants";
import { DevicesSummaryResponse } from "../dto/devices-summary-response.dto";
import { DevicesSummaryService } from "../service/devices-summary.service";


@Component({
    selector: 'telemetry-device-summary',
    templateUrl: './devices-summary.component.html',
    styleUrl: './devices-summary.component.scss',

})

export class DevicesSummaryComponent implements OnInit {

    private readonly devicesSummaryService = inject(DevicesSummaryService);
    private error = signal<string | null>(null)
    readonly summary = signal<DevicesSummaryResponse | null>(null);
    private readonly notificationService = inject(NotificationService);

    ngOnInit(): void {
        this.devicesSummaryService.getDevicesSummary().subscribe({
            next: (response) => {
                this.summary.set(response.data);
            },
            error: (httpError) => {
                this.error.set("Unable to load devices summary.");
                this.notificationService.error({
                    message: httpError.error?.message ?? MessageDefaultConstants.devicesSummary.error
                });
            }
        });
    }
}
