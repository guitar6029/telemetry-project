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
import { MatSnackBar } from '@angular/material/snack-bar';

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

    private readonly snackBar = inject(MatSnackBar);

    member = signal<OrganizationMembershipResponse | null>(null);
    error = signal<string | null>(null);
    saving = signal(false);
    loading = signal(true);
    editMode = signal<boolean>(false);


    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private memberService: MemberService
    ) { }


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
            error: (error) => {
                console.error("Failed to load member.", error);
                this.error.set("Unable to load member");
                this.loading.set(false);
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
                    //redirec back to members list
                    this.saving.set(false);
                    this.member.set(response.data);
                    this.router.navigate(['/manage/members', membershipId]);
                    this.snackBar.open(
                        'User updated succesfully!',
                        'Close',
                        {
                            duration: 3000,
                            horizontalPosition: 'right',
                            verticalPosition: 'top'
                        }
                    );
                },
                error: (error) => {
                    this.snackBar.open(
                        'User was not updated! Try Again!',
                        'Close',
                        {
                            duration: 3000,
                            horizontalPosition: 'right',
                            verticalPosition: 'top'
                        }
                    );
                    this.saving.set(false);
                }

            });
    }

}
