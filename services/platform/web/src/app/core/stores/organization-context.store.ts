import { computed, Injectable, signal } from "@angular/core";
import { OrganizationResponse } from "../../features/organization/dto/organization-response.dto";



@Injectable({
    providedIn: 'root'
})

export class OrganizationContextStore {

    private readonly _organizations = signal<OrganizationResponse[]>([]);

    readonly organizations = this._organizations.asReadonly();

    private readonly _currentOrganization = signal<OrganizationResponse | null>(null);
    readonly currentOrganization = this._currentOrganization.asReadonly();

    initialize(organizations: OrganizationResponse[],
        lastOrganizationUsed: string | null) {
        this.setOrganizations(organizations);
        if (lastOrganizationUsed === null) {
            this.setFirstOrganization();
            return;
        }
        this.setCurrentOrganization(lastOrganizationUsed)
    }

    private setOrganizations(organizations: OrganizationResponse[]): void {
        this._organizations.set(organizations);
    }

    private setCurrentOrganization(lastOrganizationUsed: string): void {
        const organization: OrganizationResponse = this.findOrganizationOrThrow(lastOrganizationUsed);
        this._currentOrganization.set(organization);
    }

    private findOrganizationOrThrow(
        organizationId: string
    ): OrganizationResponse {
        for (const organization of this.organizations()) {
            if (organization.id === organizationId) {
                return organization;
            }
        }

        throw new Error(
            `Organization ${organizationId} was not found in OrganizationContextStore.`
        );
    }

    private setFirstOrganization(): void {
        const organizations = this.organizations();

        if (organizations.length === 0) {
            throw new Error(
                "OrganizationContextStore initialized without organizations."
            );
        }

        this._currentOrganization.set(organizations[0]);
    }

    readonly currentOrganizationId = computed(() =>
        this._currentOrganization()?.id ?? null
    );

    requireCurrentOrganizationId(): string {
        const organizationId = this.currentOrganizationId();

        if (organizationId === null) {
            throw new Error("Organization context is not initialized.");
        }

        return organizationId;
    }
}
