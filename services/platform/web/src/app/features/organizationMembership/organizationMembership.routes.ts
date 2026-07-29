import { Routes } from "@angular/router";
import { OrganizationMembershipListComponent } from "./pages/organization-membership-list/organization-membership-list.component";

export const ORGANIZATION_MEMBERSHIP_ROUTES: Routes = [
    {
        path: '',
        component: OrganizationMembershipListComponent
    },
    // {
    //     path: 'new',
    //     component: OrganizationMembershipMemberComponent
    // },
    // {
    //     path: 'membershipId/edit',
    //     component: OrganizationMembershipMemberComponent
    // }
];
