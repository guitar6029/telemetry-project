import { OrganizationRole } from "../../enums/organization-role";

export interface InviteRequest {
    email: string;
    role: OrganizationRole,
}
