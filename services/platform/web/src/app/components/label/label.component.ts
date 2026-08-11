import { Component, input } from "@angular/core";

@Component({
    selector: 'telemetry-label',
    templateUrl: './label.component.html'
})

export class LabelComponent {

    text = input.required<string>();
}
