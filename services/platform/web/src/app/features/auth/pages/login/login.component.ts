import { Component } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { AuthService } from "../../service/auth.service";
import { UserConstants } from "../../constants/user.constants";
import { LoginRequest } from "../../dto/login-request.dto";
import { Router, RouterLink } from "@angular/router";
import { MatAnchor, MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";


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
            next: () => {
                this.router.navigate(['/dashboard']);
            },
            error: () => {
                this.loginError = true;
            }
        })
    }

}
