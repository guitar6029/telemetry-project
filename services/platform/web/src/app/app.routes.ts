import { Routes } from '@angular/router';
import { AUTH_ROUTES } from './features/auth/auth.routes';
import { DashboardComponent } from './features/dashboard/pages/dashboard/dashboard.component';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
    {
        path: 'auth',
        children: AUTH_ROUTES
    },
    {
        path: 'app',
        canActivate: [authGuard],
        children: [
            {
                path: 'dashboard',
                component: DashboardComponent
            },
            {
                path: 'organizations',
                loadChildren: () => import('./features/organization/organization.routes').then(m => m.ORGANIZATION_ROUTES)
            },
            {
                path: 'manage',
                children: [
                    {
                        path: 'members',
                        loadChildren: () =>
                            import('./features/organization-membership/organization-membership.routes')
                                .then(m => m.ORGANIZATION_MEMBERSHIP_ROUTES)
                    },

                ]
            }
        ]
    }

];
