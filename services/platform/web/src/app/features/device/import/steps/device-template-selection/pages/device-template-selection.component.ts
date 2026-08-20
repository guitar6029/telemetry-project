import { Component, inject, } from "@angular/core";
import { SearchComponent } from "../../../../../../components/search/search.component";
import { DeviceTemplateSelectionService } from "../service/device-template-selection.service";
import { PageComponent } from "../../../../../../components/page/page.component";

@Component({
    selector: 'telemetry-device-template-selection',
    imports: [SearchComponent, PageComponent],
    templateUrl: './device-template-selection.component.html'
})

export class DeviceTemplateSelectionComponent {

    private readonly deviceTemplateSearchService = inject(DeviceTemplateSelectionService);

    searchDeviceTemplateByQuery(query: string): void {
        this.deviceTemplateSearchService
            .getDeviceTemplatesBySearchQuery(query)
            .subscribe(response => {
                console.log('device templates:', response);
            });
    }


}
