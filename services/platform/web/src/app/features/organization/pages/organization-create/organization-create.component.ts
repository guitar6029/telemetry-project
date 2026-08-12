import { Component, inject } from "@angular/core";
import { OrganizationService } from "../../service/organization.service";
import { Router } from "@angular/router";
import { OrganizationCreateConstants } from "../../constants/organization-create.constants";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { OrganizationCreateRequest } from "../../dto/organization-create-request.dto";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { MessageDefaultConstants } from "../../../../constants/message.constants";
import { PageComponent } from "../../../../components/page/page.component";
import { LabelComponent } from "../../../../components/label/label.component";
import { InputComponent } from "../../../../components/input/input.component";
import { ErrorComponent } from "../../../../components/error/error.component";
import { ButtonComponent } from "../../../../components/button/button.component";
import { ButtonType } from "../../../../components/button/types/button-type.types";
import { ButtonStyle } from "../../../../components/button/types/button-style.types";

@Component({
    selector: 'telemetry-organization-create',
    imports: [
        ReactiveFormsModule,
        PageComponent,
        LabelComponent,
        InputComponent,
        ErrorComponent,
        ButtonComponent
    ],
    templateUrl: './organization-create.component.html',
    styleUrl: './organization-create.component.scss'
})

export class OrganizationCreateComponent {
    readonly OrganizationConstants = OrganizationCreateConstants;


    private readonly organizationService = inject(OrganizationService);
    private readonly router = inject(Router)
    private readonly notificationService = inject(NotificationService);
    protected readonly ButtonType = ButtonType;
    protected readonly ButtonStyle = ButtonStyle;

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
