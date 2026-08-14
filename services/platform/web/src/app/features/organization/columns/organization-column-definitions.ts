import { OrganizationResponse } from "../dto/organization-response.dto";
import { Column } from "../../../components/table/types/column.types";
import { ColumnType } from "../../../components/table/enums/column-type.enums";

export const OrganizationColumnDefinitions: Column<OrganizationResponse>[] = [
    { field: 'name', header: 'Name', type: ColumnType.TEXT },
    { field: 'slug', header: 'Slug', type: ColumnType.TEXT },
    { field: 'createdAt', header: 'Created At', type: ColumnType.DATE },
    { field: 'updatedAt', header: 'Updated At', type: ColumnType.DATE }
];
