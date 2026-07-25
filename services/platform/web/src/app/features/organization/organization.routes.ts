import { Routes } from "@angular/router";
import { OrganizationListComponent } from "./pages/organization-list/organization-list.component";
import { OrganizationDetailsComponent } from "./pages/organization-details/organization-details.component";
import { OrganizationCreateComponent } from "./pages/organization-create/organization-create.component";

export const ORGANIZATION_ROUTES: Routes = [
    {
        path: '',
        component: OrganizationListComponent
    },
    {
        path: 'create',
        component: OrganizationCreateComponent
    },
    {
        path: ':organizationId',
        component: OrganizationDetailsComponent
    }
];
