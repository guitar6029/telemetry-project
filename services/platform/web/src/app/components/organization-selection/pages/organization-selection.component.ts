import { Component, inject } from "@angular/core";
import { OrganizationContextStore } from "../../../core/stores/organization-context.store";

@Component({
    selector: 'telemetry-organization-selection',
    templateUrl: './organization-selection.component.html',
    styleUrl: './organization-selection.component.scss'
})
export class OrganizationSelectionComponent {

    private readonly organizationContext = inject(OrganizationContextStore);

    readonly organizations = this.organizationContext.organizations;
    readonly currentOrganization = this.organizationContext.currentOrganization;

    changeOrganization(organizationId: string): void {
        this.organizationContext.changeCurrentOrganization(organizationId);
    }
}
