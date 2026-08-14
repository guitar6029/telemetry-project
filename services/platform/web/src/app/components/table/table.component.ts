import { Component, input } from "@angular/core";
import { Column } from "./types/column.types";
import { TextColumnComponent } from "./columns/text/text-column.component";
import { ColumnType } from "./enums/column-type.enums";
import { DateColumnComponent } from "./columns/text/date/date-column.component";
import { NgComponentOutlet } from "@angular/common";





@Component({
    selector: 'telemetry-table',
    templateUrl: './table.component.html',
    imports: [NgComponentOutlet]
})


export class TableComponent<T> {

    protected readonly ColumnType = ColumnType;
    protected readonly ColumnComponentMap = {
        [ColumnType.TEXT]: TextColumnComponent,
        [ColumnType.DATE]: DateColumnComponent
    };

    data = input.required<T[]>();
    columns = input.required<Column<T>[]>()



}

