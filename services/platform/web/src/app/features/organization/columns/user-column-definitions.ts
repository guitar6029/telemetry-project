import { Column } from "../../../components/table/types/column.types";
import { ColumnType } from "../../../components/table/enums/column-type.enums";
import { OrganizationMembershipResponse } from "../../organization-membership/dto/organization-membership-response.dto";

export const OrganizationMembershipColumnDefinitions:
    Column<OrganizationMembershipResponse>[] = [
        {
            field: 'id', header: 'ID', type: ColumnType.LINK, routerLink: (user) => [
                '/app/manage/members',
                user.id
            ]
        },
        { field: 'organizationId', header: 'Organization ID', type: ColumnType.TEXT },
        { field: 'userId', header: 'User ID', type: ColumnType.TEXT },
        { field: 'firstName', header: 'First Name', type: ColumnType.TEXT },
        { field: 'lastName', header: 'Last Name', type: ColumnType.TEXT },
        { field: 'email', header: 'Email', type: ColumnType.TEXT },
        { field: 'role', header: 'Role', type: ColumnType.TEXT },
        { field: 'status', header: 'Status', type: ColumnType.TEXT },
        { field: 'createdAt', header: 'Created At', type: ColumnType.DATE },
        { field: 'updatedAt', header: 'Updated At', type: ColumnType.DATE }
    ];
