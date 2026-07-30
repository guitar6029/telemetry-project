import { OrganizationRole } from "../../enums/organization-role";

export interface InviteResponse {
    email: string;
    role: OrganizationRole;
    createdAt: string;
}
