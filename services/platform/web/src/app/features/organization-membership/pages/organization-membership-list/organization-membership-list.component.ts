import { Component, inject, OnInit, signal } from "@angular/core";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { MatTableModule } from "@angular/material/table";
import { RouterLink } from "@angular/router";
import { EmptyStateComponent } from "../../../../common/components/empty-state/empty-state.component";
import { OrganizationMembershipService } from "../../service/organization-membership.service";

import { OrganizationMembershipResponse } from "../../dto/organization-membership-response.dto";
import { MatIcon } from "@angular/material/icon";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { ProfileStore } from "../../../../core/stores/profile.store";


@Component({
    selector: 'telemetry-organization-membership-list',
    imports: [
        MatTableModule,
        MatPaginatorModule,
        EmptyStateComponent,
        RouterLink,
        MatIcon
    ],
    templateUrl: './organization-membership-list.component.html',
    styleUrl: './organization-membership-list.component.scss'
})
// // later
// // type Role =


export class OrganizationMembershipListComponent implements OnInit {

    /**
     * if user is part of multiple memberships under given organizations
     * they will see a select dropdown to switch between organizations
     * if only under one , then no UI for this is provided
    */

    organizationName = signal<string | null>(null);
    users = signal<OrganizationMembershipResponse[]>([]);
    //something like this
    //organizations = signal<OrganizationResponse[]>([]);
    page = signal(0);
    pageSize = signal(10);
    total = signal(0);

    error = signal<string | null>(null);



    displayedColumns: string[] = [
        'id',
        'organizationId',
        'userId',
        'firstName',
        'lastName',
        'email',
        'role',
        'status',
        'createdAt',
        'updatedAt'
    ]


    private readonly organizationMembershipService = inject(OrganizationMembershipService)
    private readonly notificationService = inject(NotificationService)


    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(
        page = this.page(),
        size = this.pageSize()
    ) {

        this.organizationMembershipService.getOrganizationMemberships(
            page,
            size
        ).subscribe({
            next: (response) => {
                this.users.set(response.data);
            },
            error: (httpError) => {
                this.error.set("Unable to load organization membership");
                this.notificationService.error({
                    message: httpError.error?.message ?? "Unable to load organization membership."
                })
            }
        })
    }

    onPageChange(event: PageEvent): void {
        this.loadUsers(
            event.pageIndex,
            event.pageSize
        )
    }


}
