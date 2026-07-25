import { Routes } from "@angular/router";
import { OrganizationListComponent } from "./pages/organization-list/organization-list.component";
import { OrganizationDetailsComponent } from "./pages/organization-details/organization-details.component";

export const ORGANIZATION_ROUTES: Routes = [
    {
        path: '',
        component: OrganizationListComponent
    },
    {
        path: ':organizationId',
        component: OrganizationDetailsComponent
    }
];
