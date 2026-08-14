import { Component, input } from "@angular/core";
import { Column } from "./types/column.types";
import { TextColumnComponent } from "./columns/text/text-column.component";
import { ColumnType } from "./enums/column-type.enums";
import { DateColumnComponent } from "./columns/date/date-column.component";
import { NgComponentOutlet } from "@angular/common";
import { LinkColumnComponent } from "./columns/link/link.component";





@Component({
    selector: 'telemetry-table',
    templateUrl: './table.component.html',
    imports: [NgComponentOutlet]
})


export class TableComponent<T> {

    protected readonly ColumnType = ColumnType;
    protected readonly ColumnComponentMap = {
        [ColumnType.TEXT]: TextColumnComponent,
        [ColumnType.DATE]: DateColumnComponent,
        [ColumnType.LINK]: LinkColumnComponent
    };

    protected getColumnInputs(row: T, column: Column<T>) {
        return {
            value: row[column.field],
            ...(column.routerLink
                ? { routerLink: column.routerLink(row) }
                : {})
        };
    }

    data = input.required<T[]>();
    columns = input.required<Column<T>[]>()



}

