import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { PageComponent } from "../../../../components/page/page.component";

@Component({
    selector: 'telemetry-device-templates-overview',
    templateUrl: './device-templates-overview.component.html',
    styleUrl: './device-templates-overview.component.scss',
    imports: [RouterLink, PageComponent]
})

export class DeviceTemplatesOverviewComponent {

}
