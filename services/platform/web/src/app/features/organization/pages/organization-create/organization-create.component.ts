import { Component, inject } from "@angular/core";
import { OrganizationService } from "../../service/organization.service";
import { Router } from "@angular/router";
import { OrganizationCreateConstants } from "../../constants/organization-create.constants";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { OrganizationCreateRequest } from "../../dto/organization-create-request.dto";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { MessageDefaultConstants } from "../../../../constants/message.constants";

@Component({
    selector: 'telemetry-organization-create',
    imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule
    ],
    templateUrl: './organization-create.component.html',
    styleUrl: './organization-create.component.scss'
})

export class OrganizationCreateComponent {
    readonly OrganizationConstants = OrganizationCreateConstants;


    private organizationService = inject(OrganizationService);
    private router = inject(Router)
    private notificationService = inject(NotificationService);

    organizationForm = new FormGroup(
        {
            name: new FormControl('', {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(OrganizationCreateConstants.NAME_MIN_LENGTH),
                    Validators.maxLength(OrganizationCreateConstants.NAME_MAX_LENGTH),
                ]
            }),
            slug: new FormControl('', {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(OrganizationCreateConstants.SLUG_MIN_LENGTH),
                    Validators.maxLength(OrganizationCreateConstants.SLUG_MAX_LENGTH),
                ]
            })
        }
    )

    create(): void {
        if (this.organizationForm.invalid) {
            return;
        }

        const { name, slug } = this.organizationForm.getRawValue();

        const request: OrganizationCreateRequest = {
            name,
            slug
        }

        this.organizationService.createOrganization(request).subscribe({
            next: (response) => {
                this.router.navigate([`/organizations/${response.data.id}`])
                this.notificationService.success({
                    message: MessageDefaultConstants.organization.creation.success,
                });
            },
            error: (httpError) => {
                this.notificationService.error({
                    message: httpError.error?.message ?? MessageDefaultConstants.organization.creation.error,


                });

            }
        })

    }
}
