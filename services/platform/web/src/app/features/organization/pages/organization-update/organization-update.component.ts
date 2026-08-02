import { Component, inject, OnInit, signal } from "@angular/core";
import { OrganizationService } from "../../service/organization.service";
import { ActivatedRoute, Router } from "@angular/router";
import { OrganizationCreateConstants } from "../../constants/organization-create.constants";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { OrganizationUpdateRequest } from "../../dto/organization-update-request.dto";
import { MessageDefaultConstants } from "../../../../constants/message.constants";
import { NotificationService } from "../../../../common/notification/service/notification.service";

@Component({
    selector: 'telemetry-organization-update',
    imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule
    ],
    templateUrl: './organization-update.component.html',
    styleUrl: './organization-update.component.scss'
})

export class OrganizationUpdateComponent implements OnInit {
    readonly OrganizationConstants = OrganizationCreateConstants;
    error = signal<string | null>(null);
    organizationId: string | null = null;


    private readonly organizationService = inject(OrganizationService,)
    private readonly router = inject(Router)
    private readonly route = inject(ActivatedRoute)
    private readonly notificationService = inject(NotificationService);


    ngOnInit(): void {
        this.organizationId = this.route.snapshot.paramMap.get('organizationId');

        if (this.organizationId) {
            this.loadOrganization(this.organizationId);
        } else {
            this.error.set('Organization ID is missing.');
            this.notificationService.error({
                message: MessageDefaultConstants.organization.details.errorId
            });
        }
    }

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

    loadOrganization(organizationId: string): void {
        this.organizationService
            .getOrganization(organizationId)
            .subscribe({
                next: (response) => {
                    this.organizationForm.patchValue({
                        name: response.data.name,
                        slug: response.data.slug
                    });
                },
                error: (httpError) => {
                    this.notificationService.error({
                        message: httpError.error?.message ?? MessageDefaultConstants.organization.update.error
                    });
                }
            });
    }

    update(): void {
        if (this.organizationForm.invalid || !this.organizationId) {
            return;
        }

        const { name, slug } = this.organizationForm.getRawValue();

        const request: OrganizationUpdateRequest = {
            name,
            slug
        }

        this.organizationService
            .updateOrganization(this.organizationId, request)
            .subscribe({
                next: (response) => {
                    this.router.navigate([`/organizations/${response.data.id}`])
                    this.notificationService.success({
                        message: response?.message ?? MessageDefaultConstants.organization.update.success
                    });
                },
                error: (httpError) => {
                    this.notificationService.error({
                        message: httpError.error?.message ?? MessageDefaultConstants.organization.update.error
                    });

                }
            })

    }
}
