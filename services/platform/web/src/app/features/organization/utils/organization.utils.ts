import { OrganizationResponse } from "../dto/organization-response.dto";

export function compareOrganizations(
    a: OrganizationResponse,
    b: OrganizationResponse
): boolean {
    return a.id === b.id;
}
