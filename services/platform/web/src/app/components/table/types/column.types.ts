import { ColumnType } from "../enums/column-type.enums";

export interface Column<T> {
    field: keyof T;
    header: string;
    type: ColumnType;
}
