import { Component, OnInit, signal } from "@angular/core";
import { OrganizationResponse } from "../../dto/organization-response";
import { OrganizationService } from "../../services/organization.service";
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";

@Component({
    selector: 'app-organization-list',
    imports: [
        MatTableModule,
        MatPaginatorModule
    ],
    templateUrl: './organization-list.component.html',
    styleUrl: './organization-list.component.scss'
})

export class OrganizationListComponent implements OnInit {
    organizations = signal<OrganizationResponse[]>([]);

    page = signal(0);
    pageSize = signal(10);
    total = signal(0);

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

    loadOrganizations(
        page = this.page(),
        size = this.pageSize()
    ): void {
        this.organizationService.getOrganizations(page, size).subscribe({
            next: (response) => {
                this.organizations.set(response.data);
                this.page.set(response.page);
                this.pageSize.set(response.size);
                this.total.set(response.total);

            },
            error: (error) => {
                console.error('Failed to load organizations', error);
            }
        })
    }

    onPageChange(event: PageEvent): void {
        this.loadOrganizations(
            event.pageIndex,
            event.pageSize
        )
    }


}
