import { Component } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { AuthService } from "../../services/auth.service";
import { UserConstants } from "../../constants/user.constants";
import { LoginRequest } from "../../dto/login-request";
import { LoginResponse } from "../../dto/login-response";
import { MatAnchor, MatButtonModule, MatFormFieldModule, MatInputModule } from "../../../../shared/material/material.imports";
import { RouterLink } from "@angular/router";


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

    constructor(
        private authService: AuthService
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

    //also observable since we are waiting for the token and then redirect to home
    // we could creat a dummy page for now , just says hello
    login(): void {

        if (this.loginForm.invalid) {
            return;
        }

        const request: LoginRequest = this.loginForm.getRawValue();
        this.authService.login(request).subscribe({
            next: (response: LoginResponse) => {
                console.log(response);
            }
        })
    }

}
