import { Component } from "@angular/core";
import { PageComponent } from "../../../../components/page/page.component";

@Component({
    selector: 'telemetry-device-import-detail',
    imports: [PageComponent],
    template: `
        <telemetry-page>
            <h1>Device Import Details</h1>
        </telemetry-page>
    `
})
export class DeviceImportDetailComponent {
}
