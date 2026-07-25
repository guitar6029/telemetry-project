import { Routes } from "@angular/router";
import { OrganizationListComponent } from "./pages/organization-list/organization-list.component";

export const ORGANIZATION_ROUTES: Routes = [
    {
        path: '',
        component: OrganizationListComponent
    }
];
