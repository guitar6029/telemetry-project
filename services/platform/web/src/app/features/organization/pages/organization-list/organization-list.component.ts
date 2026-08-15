import { Component, inject, OnInit, signal } from "@angular/core";
import { OrganizationResponse } from "../../dto/organization-response.dto";
import { OrganizationService } from "../../service/organization.service";
import { EmptyStateComponent } from "../../../../common/components/empty-state/empty-state.component";
import { RouterLink } from "@angular/router";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { MessageDefaultConstants } from "../../../../constants/message.constants";
import { ButtonComponent } from "../../../../components/button/button.component";
import { TableComponent } from "../../../../components/table/table.component";
import { OrganizationColumnDefinitions } from "../../columns/organization-column-definitions";
import { PageComponent } from "../../../../components/page/page.component";
import { PaginationComponent } from "../../../../components/pagination/pagination.component";
import { DEFAULT_PAGE_SIZE } from "../../../../components/pagination/constants/pagination.constants";

@Component({
    selector: 'telemetry-organization-list',
    imports: [
        ButtonComponent,
        EmptyStateComponent,
        RouterLink,
        TableComponent,
        PageComponent,
        PaginationComponent
    ],
    templateUrl: './organization-list.component.html',
    styleUrl: './organization-list.component.scss'
})

export class OrganizationListComponent implements OnInit {
    organizations = signal<OrganizationResponse[]>([]);
    protected readonly organizationColumns = OrganizationColumnDefinitions;
    page = signal(0);
    size = signal(DEFAULT_PAGE_SIZE);
    total = signal(0);
    totalPages = signal(0);

    error = signal<string | null>(null);


    private readonly organizationService = inject(OrganizationService)
    private readonly notificationService = inject(NotificationService)

    ngOnInit(): void {
        this.loadOrganizations();
    }

    loadOrganizations(
        page = this.page(),
        size = this.size()
    ): void {
        this.error.set(null);
        this.organizationService.getOrganizations(page, size).subscribe({
            next: (response) => {
                this.organizations.set(response.data);
                this.page.set(response.page);
                this.size.set(response.size);
                this.total.set(response.total);
                this.totalPages.set(response.totalPages);

            },
            error: (httpError) => {
                this.error.set("Unable to load organizations.")
                this.notificationService.error({
                    message: httpError.error?.message ?? MessageDefaultConstants.organizations.list.error
                });
            }
        })
    }


    protected onPageChange(page: number) {
        this.page.set(page);
        this.loadOrganizations();
    }

    protected onSizeChange(size: number) {
        this.size.set(size);
        this.page.set(0);
        this.loadOrganizations();
    }

}
