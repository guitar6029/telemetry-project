import { Component, inject, signal } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { UserConstants } from "../../../auth/constants/user.constants";
import { OrganizationRole } from "../../enum/organization-role.enum";
import { InviteRequest } from "../dto/invite-request.dto";
import { InviteService } from "../service/invite.service";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { PageComponent } from "../../../../components/page/page.component";
import { PageHeaderComponent } from "../../../../components/page/page-header/page-header.component";
import { ButtonType } from "../../../../components/button/types/button-type.types";
import { ButtonStyle } from "../../../../components/button/types/button-style.types";
import { InputType } from "../../../../components/input/types/input-type.types";
import { LabelComponent } from "../../../../components/label/label.component";
import { InputComponent } from "../../../../components/input/input.component";
import { ErrorComponent } from "../../../../components/error/error.component";
import { ButtonComponent } from "../../../../components/button/button.component";


@Component({
    selector: 'telemetry-invite',
    imports: [
        ReactiveFormsModule,
        PageComponent,
        PageHeaderComponent,
        LabelComponent,
        InputComponent,
        ErrorComponent,
        ButtonComponent
    ],
    templateUrl: './invite.component.html',
    styleUrl: './invite.component.scss'
})

export class InviteFormComponent {


    error = signal<string | null>(null);
    loading = signal(false)
    protected readonly OrganizationRole = OrganizationRole;
    private readonly inviteService = inject(InviteService);
    private readonly notificationService = inject(NotificationService);
    protected readonly ButtonType = ButtonType
    protected readonly ButtonStyle = ButtonStyle
    protected readonly InputType = InputType



    inviteForm = new FormGroup({
        email: new FormControl('', {
            nonNullable: true,
            validators: [
                Validators.required,
                Validators.email,
                Validators.maxLength(UserConstants.EMAIL_MAX_LENGTH)
            ]
        }),
        role: new FormControl(
            { value: OrganizationRole.MEMBER, disabled: false },
            {
                nonNullable: true,
                validators: [Validators.required]
            }
        )
    })

    sendInvite(): void {



        if (this.inviteForm.invalid) {
            this.inviteForm.markAllAsTouched();
            return;
        }

        const request: InviteRequest = {
            email: this.inviteForm.controls.email.value,
            role: this.inviteForm.controls.role.value
        };

        this.loading.set(true);
        this.inviteService.sendInvite(request).subscribe({
            next: (response) => {
                this.loading.set(false);
                this.notificationService.success({
                    message: `Invitation sent to ${response.data.email} successfully!`,
                });

            },
            error: (httpError) => {
                this.loading.set(false);
                this.notificationService.error({
                    message: httpError.error?.message ?? httpError.message
                });

            }
        })
    }
}
