import { Component, OnInit, signal } from "@angular/core";
import { OrganizationResponse } from "../../dto/organization-response";
import { OrganizationService } from "../../services/organization.service";
import { MatTableModule } from "@angular/material/table";

@Component({
    selector: 'app-organization-list',
    imports: [
        MatTableModule
    ],
    templateUrl: './organization-list.component.html',
    styleUrl: './organization-list.component.scss'
})

export class OrganizationListComponent implements OnInit {
    organizations = signal<OrganizationResponse[]>([]);

    displayedColumns: string[] = [
        'name',
        'slug',
        'createdAt',
        'updatedAt'
    ]

    constructor(
        private organizationService: OrganizationService
    ) { }

    ngOnInit(): void {
        this.loadOrganizations();
    }

    loadOrganizations(): void {
        this.organizationService.getOrganizations().subscribe({
            next: (response) => {
                this.organizations.set(response.data);

            },
            error: (error) => {
                console.error('Failed to load organizations', error);
            }
        })
    }
}
