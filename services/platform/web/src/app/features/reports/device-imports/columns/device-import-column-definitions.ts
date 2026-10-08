import { Column } from "../../../../components/table/types/column.types";
import { ColumnType } from "../../../../components/table/enums/column-type.enums";
import { DeviceImportResponse } from "../dto/device-import-response.dto";

export const DeviceImportColumnDefinitions: Column<DeviceImportResponse>[] = [
    {
        field: 'filename',
        header: 'Filename',
        type: ColumnType.LINK,
        routerLink: (deviceImport) => [
            '/app/reports/device-imports',
            deviceImport.importId
        ]
    },
    { field: 'importMode', header: 'Import Mode', type: ColumnType.TEXT },
    { field: 'status', header: 'Status', type: ColumnType.TEXT },
    { field: 'submittedAt', header: 'Submitted At', type: ColumnType.TEXT },
    { field: 'completedAt', header: 'Completed At', type: ColumnType.TEXT }
];
