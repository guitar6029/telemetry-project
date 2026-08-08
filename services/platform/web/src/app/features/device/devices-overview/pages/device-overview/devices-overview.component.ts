import { Component } from "@angular/core";
import { DevicesSummaryComponent } from "../../components/device-summary/pages/devices-summary.component";

@Component({
    selector: 'telemetry-devices-overview',
    templateUrl: './devices-overview.component.html',
    styleUrl: './devices-overview.component.scss',
    imports: [DevicesSummaryComponent]
})

export class DevicesOverviewComponent {


}
