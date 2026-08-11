import { Component, inject } from "@angular/core";
import { OrganizationContextStore } from "../../../core/stores/organization-context.store";
import { FormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";

@Component({
    selector: 'telemetry-organization-selection',
    imports: [MatFormFieldModule, MatSelectModule, MatInputModule, FormsModule],
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
