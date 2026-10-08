import { Column } from "../../../../components/table/types/column.types";
import { ColumnType } from "../../../../components/table/enums/column-type.enums";
import { DeviceImportErrorResponse } from "../dto/device-import-error-response.dto";

export const DeviceImportErrorColumnDefinitions: Column<DeviceImportErrorResponse>[] = [
    { field: "rowNumber", header: "CSV Row Number", type: ColumnType.TEXT },
    { field: "messages", header: "Error Messages", type: ColumnType.TEXT }
];
