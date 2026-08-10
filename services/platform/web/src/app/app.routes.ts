import { Routes } from '@angular/router';
import { AUTH_ROUTES } from './features/auth/auth.routes';
import { DashboardComponent } from './features/dashboard/pages/dashboard/dashboard.component';
import { authGuard } from './core/guards/auth.guard';
import { AppLayoutComponent } from './layout/app-layout.component';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'app/dashboard'
    },
    {
        path: 'auth',
        canActivate: [guestGuard],
        children: AUTH_ROUTES,
    },
    {
        path: 'app',
        canActivate: [authGuard],
        component: AppLayoutComponent,
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
                path: 'devices',
                loadChildren: () => import('./features/device/device.routes').then(m => m.DEVICE_ROUTES)
            },
            {
                path: 'device-templates',
                loadChildren: () => import('./features/device-template/device-templates.routes').then(m => m.DEVICE_TEMPLATES_ROUTES)
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
