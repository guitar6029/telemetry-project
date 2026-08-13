import { MembershipStatus } from "../../enum/membership-status.enum";
import { OrganizationRole } from "../../enum/organization-role.enum";

export interface CreateOrganizationMembershipRequest {
    firstName: string;
    lastName: string;
    email: string;
    role: OrganizationRole;
    status: MembershipStatus;
}
