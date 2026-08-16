import { ButtonComponent } from "../../../../components/button/button.component";
import { Component, inject, OnInit, signal } from "@angular/core";
import { DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from "../../../../components/pagination/constants/pagination.constants";
import { EmptyStateComponent } from "../../../../common/components/empty-state/empty-state.component";
import { MessageDefaultConstants } from "../../../../constants/message.constants";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { OrganizationColumnDefinitions } from "../../columns/organization-column-definitions";
import { OrganizationResponse } from "../../dto/organization-response.dto";
import { OrganizationService } from "../../service/organization.service";
import { PageComponent } from "../../../../components/page/page.component";
import { PaginationComponent } from "../../../../components/pagination/pagination.component";
import { PaginationState } from "../../../../components/pagination/types/pagination.types";
import { RouterLink } from "@angular/router";
import { TableComponent } from "../../../../components/table/table.component";
import { IconName } from "../../../../components/icon/icon.enums";
import { ErrorComponent } from "../../../../components/error/error.component";

@Component({
    selector: 'telemetry-organization-list',
    imports: [
        ButtonComponent,
        EmptyStateComponent,
        RouterLink,
        TableComponent,
        PageComponent,
        PaginationComponent,
        ErrorComponent
    ],
    templateUrl: './organization-list.component.html',
    styleUrl: './organization-list.component.scss'
})

export class OrganizationListComponent implements OnInit {
    organizations = signal<OrganizationResponse[]>([]);
    protected readonly IconName = IconName;
    protected readonly organizationColumns = OrganizationColumnDefinitions;
    pagination = signal<PaginationState>({
        page: DEFAULT_PAGE,
        size: DEFAULT_PAGE_SIZE,
        total: 0,
        totalPages: 0
    });

    error = signal<string | null>(null);


    private readonly organizationService = inject(OrganizationService)
    private readonly notificationService = inject(NotificationService)

    ngOnInit(): void {
        this.loadOrganizations();
    }

    loadOrganizations(
        page = this.pagination().page,
        size = this.pagination().size
    ): void {
        this.error.set(null);
        this.organizationService.getOrganizations(page, size).subscribe({
            next: (response) => {
                this.organizations.set(response.data);
                this.pagination.set({
                    page: response.page,
                    size: response.size,
                    total: response.total,
                    totalPages: response.totalPages
                });



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
        this.pagination.update(state => ({
            ...state,
            page
        }))
        this.loadOrganizations();

    }

    protected onSizeChange(size: number) {
        this.pagination.update(state => ({
            ...state,
            size,
            page: 0
        }))

        this.loadOrganizations();
    }
}
