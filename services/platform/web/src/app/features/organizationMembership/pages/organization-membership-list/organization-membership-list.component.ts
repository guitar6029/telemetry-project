import { Component, OnInit, signal } from "@angular/core";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { MatTableModule } from "@angular/material/table";
import { RouterLink } from "@angular/router";
import { EmptyStateComponent } from "../../../../common/components/empty-state/empty-state.component";
import { OrganizationMembershipService } from "../../services/organization-membership.service";

import { OrganizationMembershipResponse } from "../../dto/organization-membership-response";


@Component({
    selector: 'app-organization-membership-list',
    imports: [
        MatTableModule,
        MatPaginatorModule,
        EmptyStateComponent,
        RouterLink
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

    constructor(
        private organizationMembershipService: OrganizationMembershipService
    ) { }

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(
        page = this.page(),
        size = this.pageSize()
    ) {
        this.organizationMembershipService.getOrganizationMemberships(
            "47dbd192-2342-4d50-ae0d-3407db4f274b",
            page,
            size
        ).subscribe({
            next: (response) => {
                this.users.set(response.data);
            },
            error: (error) => {
                this.error.set("Unable to load organization membership");
                console.error('Failed to load organization membership', error);
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

// type User = {
//     id: string;
//     organizationId: string;
//     userId: string;
//     firstName: string;
//     lastName: string;
//     email: string;
//     role: OrganizationRole;
//     status: MembershipStatus;
//     createdAt: Date,
//     updatedAt: Date;

// }
