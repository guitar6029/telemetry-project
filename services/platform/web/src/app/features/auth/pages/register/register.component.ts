import { Component } from "@angular/core";
import { AuthService } from "../../service/auth.service";
import { AbstractControl, ValidationErrors, ValidatorFn, FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { UserConstants } from "../../constants/user.constants";
import { RegisterRequest } from "../../dto/register-request.dto";
import { Router, RouterLink } from "@angular/router";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";

@Component({
    selector: 'app-register',
    imports: [
        ReactiveFormsModule,
        RouterLink,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
    ],
    templateUrl: './register.component.html',
    styleUrl: './register.component.scss'
})

export class RegisterComponent {
    readonly UserConstants = UserConstants;
    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    registerForm = new FormGroup(
        {
            firstName: new FormControl('', {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(UserConstants.FIRST_NAME_MIN_LENGTH),
                    Validators.maxLength(UserConstants.FIRST_NAME_MAX_LENGTH)
                ]
            }),
            lastName: new FormControl('', {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(UserConstants.LAST_NAME_MIN_LENGTH),
                    Validators.maxLength(UserConstants.LAST_NAME_MAX_LENGTH)
                ]
            }),
            email: new FormControl('', {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.email,
                    Validators.maxLength(UserConstants.EMAIL_MAX_LENGTH)
                ]
            }),
            password: new FormControl('', {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(UserConstants.PASSWORD_MIN_LENGTH),
                    Validators.maxLength(UserConstants.PASSWORD_MAX_LENGTH)
                ]
            }),
            confirmPassword: new FormControl('', {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(UserConstants.PASSWORD_MIN_LENGTH),
                    Validators.maxLength(UserConstants.PASSWORD_MAX_LENGTH)
                ]
            }),
        },
        {
            validators: [passwordMatchValidator]
        }
    )


    register(): void {
        if (this.registerForm.invalid) {
            return;
        }

        const { firstName, lastName, email, password } = this.registerForm.getRawValue();

        const request: RegisterRequest = {
            firstName,
            lastName,
            email,
            password
        }

        this.authService.register(request).subscribe({
            next: () => {
                this.router.navigate(['/auth/login'])
            },
            error: (error) => {
                console.error(error);
            }
        });
    }


}

const passwordMatchValidator: ValidatorFn = (
    control: AbstractControl
): ValidationErrors | null => {
    const password = control.get("password")?.value;
    const confirmPassword = control.get("confirmPassword")?.value;

    if (password != confirmPassword) {
        return {
            passwordMismatch: true
        };
    }
    return null;
}
