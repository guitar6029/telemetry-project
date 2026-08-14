import { Component, input } from "@angular/core";

@Component({
    selector: 'telemetry-text-column',
    templateUrl: './text-column.component.html'
})
export class TextColumnComponent {

    value = input.required<unknown>();
}
