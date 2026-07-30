import { OrganizationRole } from "../enum/organization-role.enum";
import { MembershipStatus } from "../enum/membership-status.enum";

export interface OrganizationMembershipResponse {
    id: string;
    organizationId: string;
    userId: string;

    firstName: string,
    lastName: string,
    email: string,

    role: OrganizationRole;
    status: MembershipStatus;

    createdAt: string;
    updatedAt: string;
}
