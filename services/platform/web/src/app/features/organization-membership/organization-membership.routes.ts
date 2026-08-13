import { Routes } from "@angular/router";
import { OrganizationMembershipListComponent } from "./pages/organization-membership-list/organization-membership-list.component";
import { MemberFormComponent } from "./member/pages/member-form/member-form.component";
import { InviteFormComponent } from "./invite/pages/invite.component";


export const ORGANIZATION_MEMBERSHIP_ROUTES: Routes = [
    {
        path: '',
        component: OrganizationMembershipListComponent
    },
    {
        path: 'invite',
        component: InviteFormComponent
    },
    {
        path: ':membershipId',
        component: MemberFormComponent
    },
    {
        path: ':membershipId/edit',
        component: MemberFormComponent
    }
];
