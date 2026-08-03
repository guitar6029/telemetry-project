import { Routes } from "@angular/router";
import { AuthLayoutComponent } from "./components/auth-layout/auth-layout.component";
import { LoginComponent } from "./pages/login/login.component";

export const AUTH_ROUTES: Routes = [
    {
        path: '',
        component: AuthLayoutComponent,
        children: [
            {
                path: 'login',
                component: LoginComponent
            }
        ]
    }
]
