import { Component, input } from "@angular/core";
import { formatRelativeDate } from "../../../../utils/date.utils";

@Component({
    selector: 'telemetry-date-column',
    templateUrl: './date-column.component.html'
})

export class DateColumnComponent {

    value = input.required<unknown>();
    protected formatDate(value: unknown): string {
        return formatRelativeDate(value);
    }
}
