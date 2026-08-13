import { Routes } from "@angular/router";
import { OrganizationListComponent } from "./pages/organization-list/organization-list.component";
import { OrganizationFormComponent } from "./pages/organization-form/organization-form.component";

export const ORGANIZATION_ROUTES: Routes = [
    {
        path: '',
        component: OrganizationListComponent
    },
    {
        path: 'new',
        component: OrganizationFormComponent
    },
    {
        path: ':organizationId/edit',
        component: OrganizationFormComponent
    },
    {
        path: ':organizationId',
        component: OrganizationFormComponent
    }
];
