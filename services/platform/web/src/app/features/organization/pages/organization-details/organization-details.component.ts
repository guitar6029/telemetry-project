import { Component, OnInit, signal } from "@angular/core";
import { OrganizationService } from "../../services/organization.service";
import { ActivatedRoute } from "@angular/router";
import { OrganizationResponse } from "../../dto/organization-response";
import { MatCardModule } from "@angular/material/card";

@Component({
    selector: 'app-organization-details',
    imports: [
        MatCardModule
    ],
    templateUrl: './organization-details.component.html',
    styleUrl: './organization-details.component.scss'
})

export class OrganizationDetailsComponent implements OnInit {

    organization = signal<OrganizationResponse | null>(null);
    error = signal<string | null>(null);

    constructor(
        private route: ActivatedRoute,
        private organizationService: OrganizationService
    ) { }

    ngOnInit(): void {

        //get the id
        const organizationId = this.route.snapshot.paramMap.get("organizationId");

        if (organizationId) {
            this.loadOrganization(organizationId);
        } else {
            this.error.set("Organization ID is missing.")
        }

    }


    loadOrganization(organizationId: string): void {
        this.error.set(null);
        this.organizationService.getOrganization(organizationId).subscribe({
            next: (response) => {
                this.organization.set(response.data);
            },
            error: (error) => {
                console.error("Failed to load organization.");
                this.error.set("Unable to load organization.");
            }
        });

    }
}
