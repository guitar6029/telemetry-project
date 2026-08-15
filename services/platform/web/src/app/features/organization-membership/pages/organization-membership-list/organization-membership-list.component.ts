import { Component, inject, OnInit, signal } from "@angular/core";

import { RouterLink } from "@angular/router";
import { EmptyStateComponent } from "../../../../common/components/empty-state/empty-state.component";
import { OrganizationMembershipService } from "../../service/organization-membership.service";

import { OrganizationMembershipResponse } from "../../dto/organization-membership-response.dto";

import { NotificationService } from "../../../../common/notification/service/notification.service";
import { ProfileStore } from "../../../../core/stores/profile.store";
import { PageComponent } from "../../../../components/page/page.component";
import { TableComponent } from "../../../../components/table/table.component";
import { OrganizationMembershipColumnDefinitions } from "../../../organization/columns/user-column-definitions";
import { PaginationComponent } from "../../../../components/pagination/pagination.component";


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
    page = signal(0);
    size = signal(10);
    total = signal(0);
    totalPages = signal(0);

    error = signal<string | null>(null);

    private readonly organizationMembershipService = inject(OrganizationMembershipService)
    private readonly notificationService = inject(NotificationService)


    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(
        page = this.page(),
        size = this.size()
    ) {

        this.organizationMembershipService.getOrganizationMemberships(
            page,
            size
        ).subscribe({
            next: (response) => {
                this.users.set(response.data);
                this.page.set(response.page);
                this.size.set(response.size);
                this.total.set(response.total);
                this.totalPages.set(response.totalPages);
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
        this.page.set(page);
        this.loadUsers()
    }

    protected onSizeChange(size: number) {
        this.size.set(size);
        this.page.set(0);
        this.loadUsers()
    }



}
