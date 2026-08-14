import { Component, input } from "@angular/core";
import { Column } from "./types/column.types";

@Component({
    selector: 'telemetry-table',
    templateUrl: './table.component.html'
})

export class TableComponent<T> {
    data = input.required<T[]>();
    columns = input.required<Column<T>[]>()
}
