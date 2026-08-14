import { Component, input } from "@angular/core";

@Component({
    selector: 'telemetry-date-column',
    templateUrl: './date-column.component.html'
})

export class DateColumnComponent {

    value = input.required<unknown>();
    // probably do a check if string or Date ?
    // also do a helper for a human readable method ie 2 mins ago, 4 hours, 2 weeks ago.etc..
}
