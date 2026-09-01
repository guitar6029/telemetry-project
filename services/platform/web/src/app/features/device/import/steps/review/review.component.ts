import { Component, input } from "@angular/core";
import { PageComponent } from "../../../../../components/page/page.component";

@Component({
    selector: 'telemetry-review-step',
    templateUrl: './review.component.html',
    imports: [PageComponent]
})

export class ReviewStep {

    selectedDeviceTemplate = input<string>();
    selectedHierarchyNode = input<string>()
    selectedFile = input<string>();
}
