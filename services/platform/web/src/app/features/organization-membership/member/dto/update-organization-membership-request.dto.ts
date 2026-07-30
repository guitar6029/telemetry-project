import { MembershipStatus } from "../../enum/membership-status.enum";
import { OrganizationRole } from "../../enum/organization-role.enum";

export interface UpdateOrganizationMembershipRequest {
    role: OrganizationRole;
    status: MembershipStatus;
}
