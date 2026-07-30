import { OrganizationRole } from "../../enum/organization-role.enum";

export interface InviteRequest {
    email: string;
    role: OrganizationRole,
}
