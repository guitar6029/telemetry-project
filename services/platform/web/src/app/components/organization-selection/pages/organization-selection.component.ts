import { Component, computed, inject } from "@angular/core";
import { OrganizationContextStore } from "../../../core/stores/organization-context.store";
import { FormsModule } from "@angular/forms";
import { SelectComponent } from "../../select/select.component";
import { toSelectOptions } from "../../../utils/to-select-option";

@Component({
    selector: 'telemetry-organization-selection',
    imports: [FormsModule, SelectComponent],
    templateUrl: './organization-selection.component.html',
    styleUrl: './organization-selection.component.scss'
})

export class OrganizationSelectionComponent {

    private readonly organizationContext = inject(OrganizationContextStore);
    readonly organizations = this.organizationContext.organizations;
    readonly currentOrganization = this.organizationContext.currentOrganization;


    readonly currentOrganizationOption = computed(() => {
        const organization = this.currentOrganization();
        if (!organization) {
            return null;
        }

        return {
            label: organization.name,
            value: organization.id
        };
    });

    readonly organizationOptions = computed(() => {
        return toSelectOptions(this.organizations(),
            organization => organization.name,
            organization => organization.id,
        )
    })



    changeOrganization(organizationId: string): void {
        this.organizationContext.changeCurrentOrganization(organizationId);
    }

}
