import { Component } from "@angular/core";
import { DevicesSummaryComponent } from "../../components/device-summary/pages/devices-summary.component";
import { PageComponent } from "../../../../../components/page/page.component";
import { ButtonComponent } from "../../../../../components/button/button.component";

@Component({
    selector: 'telemetry-devices-overview',
    templateUrl: './devices-overview.component.html',
    styleUrl: './devices-overview.component.scss',
    imports: [DevicesSummaryComponent, PageComponent, ButtonComponent]
})

export class DevicesOverviewComponent {


}
