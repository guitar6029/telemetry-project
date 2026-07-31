import { Component, inject, OnInit, signal } from "@angular/core";
import { OrganizationResponse } from "../../dto/organization-response.dto";
import { OrganizationService } from "../../service/organization.service";
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { EmptyStateComponent } from "../../../../common/components/empty-state/empty-state.component";
import { RouterLink } from "@angular/router";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { MessageDefaultConstants } from "../../../../constants/message.constants";

@Component({
    selector: 'telemetry-organization-list',
    imports: [
        MatTableModule,
        MatPaginatorModule,
        EmptyStateComponent,
        RouterLink
    ],
    templateUrl: './organization-list.component.html',
    styleUrl: './organization-list.component.scss'
})

export class OrganizationListComponent implements OnInit {
    organizations = signal<OrganizationResponse[]>([]);

    page = signal(0);
    pageSize = signal(10);
    total = signal(0);

    error = signal<string | null>(null);

    displayedColumns: string[] = [
        'name',
        'slug',
        'createdAt',
        'updatedAt'
    ]


    private organizationService = inject(OrganizationService)
    private notificationService = inject(NotificationService)

    ngOnInit(): void {
        this.loadOrganizations();
    }

    loadOrganizations(
        page = this.page(),
        size = this.pageSize()
    ): void {
        this.error.set(null);
        this.organizationService.getOrganizations(page, size).subscribe({
            next: (response) => {
                this.organizations.set(response.data);
                this.page.set(response.page);
                this.pageSize.set(response.size);
                this.total.set(response.total);

            },
            error: (httpError) => {
                this.error.set("Unable to load organizations.")
                this.notificationService.error({
                    message: httpError.error?.message ?? MessageDefaultConstants.organizations.list.error
                });
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
