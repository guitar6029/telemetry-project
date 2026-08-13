import { ButtonComponent } from "../../../../components/button/button.component";
import { ButtonStyle } from "../../../../components/button/types/button-style.types";
import { ButtonType } from "../../../../components/button/types/button-type.types";
import { capitalize } from "../../../../utils/string.utils";
import { Component, computed, inject, OnInit, signal } from "@angular/core";
import { EmptyStateComponent } from "../../../../common/components/empty-state/empty-state.component";
import { ErrorComponent } from "../../../../components/error/error.component";
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from "@angular/forms";
import { FormMode } from "../../../../types/mode.types";
import { InputComponent } from "../../../../components/input/input.component";
import { InputType } from "../../../../components/input/types/input-type.types";
import { LabelComponent } from "../../../../components/label/label.component";
import { LoadingSpinnerComponent } from "../../../../components/loading/loading-spinner/loading-spinner.component";
import { MessageDefaultConstants } from "../../../../constants/message.constants";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { OrganizationCreateConstants } from "../../constants/organization-create.constants";
import { OrganizationCreateRequest } from "../../dto/organization-create-request.dto";
import { OrganizationResponse } from "../../dto/organization-response.dto";
import { OrganizationRole } from "../../../organization-membership/enum/organization-role.enum";
import { OrganizationService } from "../../service/organization.service";
import { OrganizationUpdateRequest } from "../../dto/organization-update-request.dto";
import { PageComponent } from "../../../../components/page/page.component";
import { Router, ActivatedRoute } from "@angular/router";
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
    selector: 'telemetry-organization-form',
    templateUrl: './organization-form.component.html',
    imports: [ReactiveFormsModule, PageComponent, LabelComponent, ErrorComponent, InputComponent, ButtonComponent, LoadingSpinnerComponent, EmptyStateComponent]
})

export class OrganizationFormComponent implements OnInit {

    protected readonly InputType = InputType;
    protected readonly OrganizationRole = OrganizationRole;
    protected readonly ButtonType = ButtonType
    protected readonly ButtonStyle = ButtonStyle;
    protected readonly capitalize = capitalize;
    readonly OrganizationConstants = OrganizationCreateConstants;
    private readonly organizationService = inject(OrganizationService,)
    private readonly router = inject(Router)
    private readonly route = inject(ActivatedRoute)
    private readonly notificationService = inject(NotificationService);
    organizationId: string | null = null;
    organization = signal<OrganizationResponse | null>(null);
    error = signal<string | null>(null);
    saving = signal(false);
    loading = signal(true);
    mode = signal<FormMode>("create");


    ngOnInit(): void {
        const path = this.route.snapshot.routeConfig?.path;
        if (path === 'new') {
            this.mode.set('create');
            this.organizationForm.enable();
            this.loading.set(false);
            return;
        }

        if (path === ':organizationId') {
            this.mode.set('view');
        }

        if (path === ':organizationId/edit') {
            this.mode.set('edit');
            this.organizationForm.enable()
        }

        const organizationId =
            this.route.snapshot.paramMap.get('organizationId');

        if (organizationId) {
            this.organizationId = organizationId;

            if (this.mode() === 'view') {
                this.organizationForm.disable();
            }

            this.loadOrganization(organizationId);
        }
    }

    readonly fieldsetTitle = computed(() => {
        switch (this.mode()) {
            case 'create':
                return "New Organization";
            case 'edit':
                return "Edit Organization";
            case 'view':
                return "Details"
        }

    })

    readonly formSubmitButtonText = computed(() => {
        switch (this.mode()) {
            case 'create':
                return 'Save';
            case 'edit':
                return 'Update'
            case 'view':
                return '';
        }
    })

    readonly onlyCreateOrEditMode = computed(() => {
        return this.mode() === 'create' || this.mode() === 'edit';
    })

    readonly formDisabled = computed(() => {
        return (
            this.organizationForm.invalid ||
            this.saving() ||
            (this.mode() === 'edit' && !this.hasChanges())
        );
    });

    organizationForm = new FormGroup(
        {
            name: new FormControl({ value: '', disabled: true }, {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(OrganizationCreateConstants.NAME_MIN_LENGTH),
                    Validators.maxLength(OrganizationCreateConstants.NAME_MAX_LENGTH),
                ]
            }),
            slug: new FormControl({ value: '', disabled: true }, {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(OrganizationCreateConstants.SLUG_MIN_LENGTH),
                    Validators.maxLength(OrganizationCreateConstants.SLUG_MAX_LENGTH),
                ]
            })
        }
    )

    readonly formValue = toSignal(
        this.organizationForm.valueChanges,
        { initialValue: this.organizationForm.getRawValue() }
    );


    loadOrganization(organizationId: string): void {
        this.organizationService
            .getOrganization(organizationId)
            .subscribe({
                next: (response) => {
                    this.organizationForm.patchValue({
                        name: response.data.name,
                        slug: response.data.slug
                    });
                    this.organization.set(response.data);
                },
                error: (httpError) => {
                    this.notificationService.error({
                        message: httpError.error?.message ?? MessageDefaultConstants.organization.update.error
                    });
                }
            });
    }

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

    hasChanges(): boolean {
        const organization = this.organization();
        const formValue = this.formValue();

        if (!organization) {
            return false;
        }

        return (
            formValue.name !== organization.name ||
            formValue.slug !== organization.slug
        );
    }
}
