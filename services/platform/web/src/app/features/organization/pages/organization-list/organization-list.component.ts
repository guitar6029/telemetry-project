import { Component, OnInit } from "@angular/core";
import { OrganizationResponse } from "../../dto/organization-response";
import { OrganizationService } from "../../services/organization.service";

@Component({
    selector: 'app-organization-list',
    imports: [],
    templateUrl: './organization-list.component.html',
    styleUrl: './organization-list.component.scss'
})

export class OrganizationListComponent implements OnInit {
    organizations: OrganizationResponse[] = [];

    constructor(
        private organizationService: OrganizationService
    ) { }

    ngOnInit(): void {
        this.loadOrganizations();
    }

    loadOrganizations(): void {
        this.organizationService.getOrganizations().subscribe({
            next: (response) => {
                this.organizations = response.data;
            },
            error: (error) => {
                console.error('Failed to load organizations', error);
            }
        })
    }
}
