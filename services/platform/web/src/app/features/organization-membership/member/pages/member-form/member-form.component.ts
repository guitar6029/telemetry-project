import { Component, inject, OnInit, signal } from "@angular/core";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { MemberService } from "../../service/member.service";
import { OrganizationMembershipResponse } from "../../../dto/organization-membership-response.dto";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { OrganizationRole } from "../../../enum/organization-role.enum";
import { MembershipStatus } from "../../../enum/membership-status.enum";
import { MatIconModule } from "@angular/material/icon";
import { EmptyStateComponent } from "../../../../../common/components/empty-state/empty-state.component";
import { MatSelectModule } from "@angular/material/select";
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { UpdateOrganizationMembershipRequest } from "../../dto/update-organization-membership-request.dto";
import { MessageDefaultConstants } from "../../../../../constants/message.constants";
import { NotificationService } from "../../../../../common/notification/service/notification.service";

@Component({
    selector: 'telemetry-member-form',
    imports: [
        ReactiveFormsModule,
        RouterLink,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatSelectModule,
        MatProgressSpinner,
        EmptyStateComponent,
    ],
    templateUrl: './member-form.component.html',
    styleUrl: './member-form.component.scss'
})
export class MemberFormComponent implements OnInit {

    readonly roleSelections = Object.values(OrganizationRole);
    readonly statusSelections = Object.values(MembershipStatus);

    member = signal<OrganizationMembershipResponse | null>(null);
    error = signal<string | null>(null);
    saving = signal(false);
    loading = signal(true);
    editMode = signal<boolean>(false);



    private readonly route = inject(ActivatedRoute);
    private readonly router = inject(Router)
    private readonly memberService = inject(MemberService);
    private readonly notificationService = inject(NotificationService)



    ngOnInit(): void {
        //get member id
        const membershipId = this.route.snapshot.paramMap.get("membershipId")
        // check if editing
        this.editMode.set(this.route.snapshot.routeConfig?.path === ':membershipId/edit');

        if (membershipId) {
            this.loadMembership(membershipId);
        } else {
            console.error("Failed to load member - missing member id");
            this.error.set("Unable to load member - missing member id");
            this.loading.set(false);
            this.notificationService.error({
                message: "Unable to load member - missing member id"
            })
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

    hasChanges(): boolean {
        const member = this.member();

        if (!member) {
            return false;
        }

        return (
            this.memberForm.controls.role.value !== member.role ||
            this.memberForm.controls.status.value !== member.status
        );
    }

    updateMembership(): void {

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

}
