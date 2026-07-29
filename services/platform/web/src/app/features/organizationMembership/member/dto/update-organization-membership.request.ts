import { MembershipStatus } from "../../enums/membership-status";
import { OrganizationRole } from "../../enums/organization-role";

export interface UpdateOrganizationMembershipRequest {
    role: OrganizationRole;
    status: MembershipStatus;
}
