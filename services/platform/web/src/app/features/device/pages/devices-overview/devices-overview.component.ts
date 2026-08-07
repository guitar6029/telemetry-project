import { Component, inject, OnInit } from "@angular/core";
import { DevicesOverviewService } from "../../service/devices-overview.service";

@Component({
    selector: 'telemetry-devices-overview',
    templateUrl: './devices-overview.component.html',
    styleUrl: './devices-overview.component.scss'
})

export class DevicesOverviewComponent implements OnInit {


    private readonly devicesOverviewService = inject(DevicesOverviewService);

    ngOnInit(): void {
        this.devicesOverviewService.getDevicesOverview().subscribe({
            next: (response) => {
                //this.organization.set(response.data);
                console.log("response : ", response);
            },
            error: (httpError) => {
                console.log(httpError)
                // this.error.set("Unable to load organization.");
                // this.notificationService.error({
                //     message: httpError.error?.message ?? MessageDefaultConstants.organization.details.error
                // });
            }
        });
    }

}
