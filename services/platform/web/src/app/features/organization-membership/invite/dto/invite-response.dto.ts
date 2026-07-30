import { OrganizationRole } from "../../enum/organization-role.enum";

export interface InviteResponse {
    email: string;
    role: OrganizationRole;
    createdAt: string;
}
