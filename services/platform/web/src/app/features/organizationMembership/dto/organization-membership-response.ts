import { OrganizationRole } from "../enums/organization-role";
import { MembershipStatus } from "../enums/membership-status";

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
