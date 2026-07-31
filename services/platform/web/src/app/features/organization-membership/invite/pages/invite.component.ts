import { Component, inject, signal } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { UserConstants } from "../../../auth/constants/user.constants";
import { OrganizationRole } from "../../enum/organization-role.enum";
import { MembershipStatus } from "../../enum/membership-status.enum";
import { InviteRequest } from "../dto/invite-request.dto";
import { InviteService } from "../service/invite.service";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
//import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { MatSelectModule } from "@angular/material/select";
//import { RouterLink } from "@angular/router";
import { MatCard, MatCardHeader, MatCardTitle, MatCardSubtitle, MatCardContent } from "@angular/material/card";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { NotificationSettingsConstants } from "../../../../common/notification/constants/notification-settings.constants";

@Component({
    selector: 'telemetry-invite',
    imports: [
        ReactiveFormsModule,
        // RouterLink,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatSelectModule,
        // MatProgressSpinner,
        MatCard,
        MatCardHeader,
        MatCardTitle,
        MatCardSubtitle,
        MatCardContent
    ],
    templateUrl: './invite.component.html',
    styleUrl: './invite.component.scss'
})

export class InviteFormComponent {


    error = signal<string | null>(null);
    loading = signal(false)
    protected readonly OrganizationRole = OrganizationRole;
    private inviteService = inject(InviteService);
    private notificationService = inject(NotificationService);



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
