import { Component, inject, signal } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { AuthService } from "../../service/auth.service";
import { UserConstants } from "../../constants/user.constants";
import { LoginRequest } from "../../dto/login-request.dto";
import { Router } from "@angular/router";

import { NotificationService } from "../../../../common/notification/service/notification.service";
import { MessageDefaultConstants } from "../../../../constants/message.constants";
import { switchMap } from "rxjs";
import { SessionService } from "../../service/session.service";
import { InputComponent } from "../../../../components/input/input.component";
import { InputType } from "../../../../components/input/types/input-type.types";
import { LabelComponent } from "../../../../components/label/label.component";
import { ButtonComponent } from "../../../../components/button/button.component";
import { ButtonType } from "../../../../components/button/types/button-type.types";


@Component({
    selector: 'telemetry-login',
    imports: [
        ReactiveFormsModule,
        InputComponent,
        LabelComponent,
        ButtonComponent
    ],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss'
})

export class LoginComponent {

    loginError = false;
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);
    private readonly notificationService = inject(NotificationService);
    private readonly sessionService = inject(SessionService);
    protected readonly InputType = InputType;
    protected readonly ButtonType = ButtonType;
    loading = signal(false);



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

        this.loading.set(true);
        this.loginError = false;

        const request: LoginRequest = this.loginForm.getRawValue();
        this.authService.login(request).pipe(

            switchMap(() => this.sessionService.initialize())

        ).subscribe({
            next: () => {
                this.router.navigate(
                    ['/app/dashboard'],
                    {
                        replaceUrl: true
                    }
                );
                this.notificationService.success({
                    message: MessageDefaultConstants.auth.login.success,
                });

                this.loading.set(false);


            },
            error: (httpError) => {
                this.loginError = true;
                this.notificationService.error({
                    message: httpError.error?.message ?? MessageDefaultConstants.auth.login.error,
                });
                this.loading.set(false);
            }
        })
    }

}

