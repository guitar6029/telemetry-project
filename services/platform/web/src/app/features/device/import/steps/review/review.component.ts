import { Component, computed, input } from "@angular/core";
import { PageComponent } from "../../../../../components/page/page.component";
import { DeviceImportMode } from "../../enums/device-import-mode.enums";
import { LabelComponent } from "../../../../../components/label/label.component";

@Component({
    selector: 'telemetry-review-step',
    templateUrl: './review.component.html',
    imports: [PageComponent, LabelComponent]
})

export class ReviewStep {
    selectedDeviceTemplate = input<string>();
    selectedHierarchyNode = input<string>()
    selectedFile = input<string>();
    selectedImportMode = input<DeviceImportMode>();

    readonly importMode = computed(() => {
        switch (this.selectedImportMode()) {
            case DeviceImportMode.SKIP_EXISTING:
                return "Skip existing"
            default:
                return "Update existing"
        }
    })
}
