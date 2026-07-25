import { Routes } from '@angular/router';
import { AUTH_ROUTES } from './features/auth/auth.routes';
import { DashboardComponent } from './features/dashboard/pages/dashboard/dashboard.component';

export const routes: Routes = [
    {
        path: 'auth',
        children: AUTH_ROUTES
    },
    {
        path: 'dashboard',
        component: DashboardComponent
    },
    {
        path: 'organizations',
        loadChildren: () => import('./features/organization/organization.routes').then(m => m.ORGANIZATION_ROUTES)
    }
];
