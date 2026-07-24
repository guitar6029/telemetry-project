import { Routes } from "@angular/router";
import { AuthLayoutComponent } from "./components/auth-layout/auth-layout.component";
import { LoginComponent } from "./pages/login/login.component";
import { RegisterComponent } from "./pages/register/register.component";

export const AUTH_ROUTES: Routes = [
    {
        path: '',
        component: AuthLayoutComponent,
        children: [
            {
                path: 'login',
                component: LoginComponent
            },
            {
                path: 'register',
                component: RegisterComponent,
            }
        ]
    }
]
