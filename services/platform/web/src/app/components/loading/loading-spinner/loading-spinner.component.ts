import { Component, input } from "@angular/core";
import { SpinnerSize } from "./enums/spinner-size.enums";

@Component({
    selector: 'telemetry-loading-spinner',
    templateUrl: './loading-spinner.component.html'
})

export class LoadingSpinnerComponent {
    size = input<SpinnerSize>(SpinnerSize.LOADING_XL);
}
