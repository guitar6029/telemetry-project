import { Component, input } from "@angular/core";

@Component({
    selector: 'telemetry-error',
    templateUrl: './error.component.html'
})

export class ErrorComponent {
    text = input.required<string>();
}
