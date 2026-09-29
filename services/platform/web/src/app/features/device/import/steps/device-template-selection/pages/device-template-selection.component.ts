import { Component, computed, inject, output, signal, } from "@angular/core";
import { SearchComponent } from "../../../../../../components/search/search.component";
import { DeviceTemplateSelectionService } from "../service/device-template-selection.service";
import { PageComponent } from "../../../../../../components/page/page.component";
import { DeviceTemplateOptionResponse } from "../../../../../device-template/dto/device-template-option-response.dto";
import { DeviceTemplateSelection } from "../../../dto/device-import.dto";

@Component({
    selector: 'telemetry-device-template-selection',
    imports: [SearchComponent, PageComponent],
    templateUrl: './device-template-selection.component.html'
})

export class DeviceTemplateSelectionComponent {

    private readonly deviceTemplateSearchService = inject(DeviceTemplateSelectionService);

    deviceTemplates = signal<DeviceTemplateOptionResponse[]>([]);

    currentSelectedTemplate = signal<DeviceTemplateSelection | null>(null);

    selectedDeviceTemplate = output<DeviceTemplateSelection>();


    readonly hasDeviceTemplates = computed(() =>
        this.deviceTemplates().length > 0
    );

    searchDeviceTemplateByQuery(query: string): void {
        this.deviceTemplateSearchService
            .getDeviceTemplatesBySearchQuery(query)
            .subscribe({
                next: (response) => {
                    this.deviceTemplates.set(response.data);
                },
                error: (httpError) => {
                    console.error(httpError)
                }
            });
    }

    selectTemplate(deviceTemplate: DeviceTemplateSelection): void {
        this.currentSelectedTemplate.set(deviceTemplate);
        this.selectedDeviceTemplate.emit(deviceTemplate);
    }


}
