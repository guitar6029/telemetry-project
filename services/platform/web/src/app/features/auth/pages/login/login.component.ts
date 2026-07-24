import { Component } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { AuthService } from "../../services/auth.service";
import { UserConstants } from "../../constants/user.constants";
import { LoginRequest } from "../../dto/login-request";
import { LoginResponse } from "../../dto/login-response";
import { MatAnchor, MatButtonModule, MatFormFieldModule, MatInputModule } from "../../../../shared/material/material.imports";
import { Router, RouterLink } from "@angular/router";


@Component({
    selector: 'app-login',
    imports: [
        ReactiveFormsModule,
        MatAnchor,
        RouterLink,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule
    ],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss'
})

export class LoginComponent {

    loginError = false;

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    loginForm = new FormGroup({
        email: new FormControl('', {
            nonNullable: true,
            validators: [
                Validators.required,
                Validators.email
            ]
        }),
        password: new FormControl('', {
            nonNullable: true, validators: [
                Validators.required,
                Validators.minLength(UserConstants.PASSWORD_MIN_LENGTH),
                Validators.maxLength(UserConstants.PASSWORD_MAX_LENGTH)
            ]
        })
    })

    login(): void {

        if (this.loginForm.invalid) {
            return;
        }

        this.loginError = false;

        const request: LoginRequest = this.loginForm.getRawValue();
        this.authService.login(request).subscribe({
            next: (response: LoginResponse) => {
                console.log(response);
                this.router.navigate(['/dashboard']);
            },
            error: () => {
                this.loginError = true;
            }
        })
    }

}
