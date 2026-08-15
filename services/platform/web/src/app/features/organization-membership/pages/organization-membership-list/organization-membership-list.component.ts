import { Component, inject, OnInit, signal } from "@angular/core";
import { DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from "../../../../components/pagination/constants/pagination.constants";
import { EmptyStateComponent } from "../../../../common/components/empty-state/empty-state.component";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { OrganizationMembershipColumnDefinitions } from "../../../organization/columns/user-column-definitions";
import { OrganizationMembershipResponse } from "../../dto/organization-membership-response.dto";
import { OrganizationMembershipService } from "../../service/organization-membership.service";
import { PageComponent } from "../../../../components/page/page.component";
import { PaginationComponent } from "../../../../components/pagination/pagination.component";
import { PaginationState } from "../../../../components/pagination/types/pagination.types";
import { RouterLink } from "@angular/router";
import { TableComponent } from "../../../../components/table/table.component";


@Component({
    selector: 'telemetry-organization-membership-list',
    imports: [
        EmptyStateComponent,
        RouterLink,
        PageComponent,
        TableComponent,
        PaginationComponent
    ],
    templateUrl: './organization-membership-list.component.html',
    styleUrl: './organization-membership-list.component.scss'
})


export class OrganizationMembershipListComponent implements OnInit {

    protected readonly userColumns = OrganizationMembershipColumnDefinitions;
    organizationName = signal<string | null>(null);
    users = signal<OrganizationMembershipResponse[]>([]);
    pagination = signal<PaginationState>({
        page: DEFAULT_PAGE,
        size: DEFAULT_PAGE_SIZE,
        total: 0,
        totalPages: 0
    });

    error = signal<string | null>(null);

    private readonly organizationMembershipService = inject(OrganizationMembershipService)
    private readonly notificationService = inject(NotificationService)


    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(
        page = this.pagination().page,
        size = this.pagination().size
    ) {

        this.organizationMembershipService.getOrganizationMemberships(
            page,
            size
        ).subscribe({
            next: (response) => {
                this.users.set(response.data);


                this.pagination.set({
                    page: response.page,
                    size: response.size,
                    total: response.total,
                    totalPages: response.totalPages
                });
            },
            error: (httpError) => {
                this.error.set("Unable to load organization membership");
                this.notificationService.error({
                    message: httpError.error?.message ?? "Unable to load organization membership."
                })
            }
        })
    }


    protected onPageChange(page: number) {
        this.pagination.update(state => ({
            ...state,
            page
        }))
        this.loadUsers()
    }

    protected onSizeChange(size: number) {
        this.pagination.update(state => ({
            ...state,
            size,
            page: 0
        }))

        this.loadUsers()
    }



}
