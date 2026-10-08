import { Component } from "@angular/core";
import { PageComponent } from "../../../../components/page/page.component";

@Component({
    selector: 'telemetry-device-import-list',
    imports: [PageComponent],
    template: `
        <telemetry-page>
            <h1>Device Imports</h1>
        </telemetry-page>
    `
})
export class DeviceImportListComponent {
}
