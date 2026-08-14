import { OrganizationResponse } from "../../../../features/organization/dto/organization-response.dto";
import { Column } from "../../types/column.types";

export const OrganizationColumns: Column<OrganizationResponse>[] = [
    { field: 'name', header: 'Name' },
    { field: 'slug', header: 'Slug' },
    { field: 'createdAt', header: 'Created At' },
    { field: 'updatedAt', header: 'Updated At' }
];
