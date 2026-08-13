import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { ButtonComponent } from "../../../../../components/button/button.component";
import { ButtonStyle } from "../../../../../components/button/types/button-style.types";
import { ButtonType } from "../../../../../components/button/types/button-type.types";
import { capitalize } from "../../../../../utils/string.utils";
import { Component, computed, inject, OnInit, signal } from "@angular/core";
import { EmptyStateComponent } from "../../../../../common/components/empty-state/empty-state.component";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { InputComponent } from "../../../../../components/input/input.component";
import { InputType } from "../../../../../components/input/types/input-type.types";
import { LabelComponent } from "../../../../../components/label/label.component";
import { LoadingSpinnerComponent } from "../../../../../components/loading/loading-spinner/loading-spinner.component";
import { MemberFormMode } from "../../types/member-form-mode.types";
import { MemberService } from "../../service/member.service";
import { MembershipStatus } from "../../../enum/membership-status.enum";
import { MessageDefaultConstants } from "../../../../../constants/message.constants";
import { NotificationService } from "../../../../../common/notification/service/notification.service";
import { OrganizationMembershipResponse } from "../../../dto/organization-membership-response.dto";
import { OrganizationRole } from "../../../enum/organization-role.enum";
import { PageComponent } from "../../../../../components/page/page.component";
import { toSignal } from "@angular/core/rxjs-interop";
import { UpdateOrganizationMembershipRequest } from "../../dto/update-organization-membership-request.dto";

@Component({
    selector: 'telemetry-member-form',
    imports: [
        ReactiveFormsModule,
        RouterLink,
        EmptyStateComponent,
        LoadingSpinnerComponent,
        PageComponent,
        ButtonComponent,
        LabelComponent,
        InputComponent
    ],
    templateUrl: './member-form.component.html',
    styleUrl: './member-form.component.scss'
})
export class MemberFormComponent implements OnInit {

    readonly roleSelections = Object.values(OrganizationRole);
    readonly statusSelections = Object.values(MembershipStatus);
    protected readonly InputType = InputType;
    protected readonly OrganizationRole = OrganizationRole;
    protected readonly ButtonType = ButtonType
    protected readonly ButtonStyle = ButtonStyle;
    protected readonly capitalize = capitalize;

    member = signal<OrganizationMembershipResponse | null>(null);
    membershipId: string | null = null;
    error = signal<string | null>(null);
    saving = signal(false);
    loading = signal(true);
    editMode = signal<boolean>(false);
    mode = signal<MemberFormMode>('view');


    private readonly route = inject(ActivatedRoute);
    private readonly router = inject(Router)
    private readonly memberService = inject(MemberService);
    private readonly notificationService = inject(NotificationService)



    ngOnInit(): void {

        const path = this.route.snapshot.routeConfig?.path;


        if (path === ':membershipId') {
            this.mode.set('view');
        }

        if (path === ':membershipId/edit') {
            this.mode.set('edit');
            this.memberForm.controls.role.enable();
            this.memberForm.controls.status.enable();
        }

        const membershipId =
            this.route.snapshot.paramMap.get('membershipId');

        if (membershipId) {
            this.membershipId = membershipId;

            if (this.mode() === 'view') {
                this.memberForm.disable();
            }

            this.loadMembership(membershipId);
        }
    }

    memberForm = new FormGroup({
        firstName: new FormControl(
            { value: '', disabled: true },
            {
                nonNullable: true
            }
        ),

        lastName: new FormControl(
            { value: '', disabled: true },
            {
                nonNullable: true
            }
        ),

        email: new FormControl(
            { value: '', disabled: true },
            {
                nonNullable: true
            }
        ),

        role: new FormControl(
            { value: OrganizationRole.MEMBER, disabled: true },
            {
                nonNullable: true,
                validators: [Validators.required]
            }
        ),

        status: new FormControl(
            { value: MembershipStatus.ACTIVE, disabled: true },
            {
                nonNullable: true,
                validators: [Validators.required]
            }
        )
    });

    loadMembership(membershipId: string): void {
        this.loading.set(true);
        this.error.set(null);
        this.memberService.getMember(membershipId).subscribe({
            next: (response) => {
                this.member.set(response.data)

                this.memberForm.patchValue({
                    firstName: response.data.firstName,
                    lastName: response.data.lastName,
                    email: response.data.email,
                    role: response.data.role,
                    status: response.data.status
                })

                if (this.editMode()) {
                    this.memberForm.controls.role.enable();
                    this.memberForm.controls.status.enable();

                }

                this.loading.set(false);
            },
            error: (httpError) => {
                this.error.set("Unable to load member");
                this.loading.set(false);
                this.notificationService.error({
                    message: httpError.error?.message ?? MessageDefaultConstants.organization.update.error
                });
            }
        })
    }

    readonly formValue = toSignal(
        this.memberForm.valueChanges,
        { initialValue: this.memberForm.getRawValue() }
    );


    hasChanges(): boolean {
        const member = this.member();
        const formValue = this.formValue();

        if (!member) {
            return false;
        }

        return (
            formValue.role !== member.role ||
            formValue.status !== member.status
        );
    }



    update(): void {

        const membershipId = this.member()?.id;

        if (!membershipId) {
            return;
        }

        const request: UpdateOrganizationMembershipRequest = {
            role: this.memberForm.controls.role.value,
            status: this.memberForm.controls.status.value
        };

        this.memberService
            .updateMember(membershipId, request)
            .subscribe({
                next: (response) => {
                    this.saving.set(false);
                    this.member.set(response.data);
                    this.router.navigate(['/manage/members', membershipId]);
                    this.notificationService.success({
                        message: response?.message ?? "Updated user successfully!"
                    })
                },
                error: (httpError) => {
                    this.notificationService.error({
                        message: httpError.error?.message ?? "User could not be updated."
                    })
                    this.saving.set(false);
                }

            });
    }

    readonly fieldsetTitle = computed(() => {
        switch (this.mode()) {
            case 'edit':
                return "Edit Membership";
            case 'view':
                return "Details"
        }

    })

    readonly formDisabled = computed(() => {
        return (
            this.memberForm.invalid ||
            this.saving() ||
            (this.mode() === 'edit' && !this.hasChanges())
        );
    });

    readonly formSubmitButtonText = computed(() => {
        switch (this.mode()) {
            case 'edit':
                return 'Update'
            case 'view':
                return '';
        }
    })

}
