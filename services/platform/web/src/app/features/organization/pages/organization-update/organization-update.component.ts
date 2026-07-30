import { Component, OnInit, signal } from "@angular/core";
import { OrganizationService } from "../../service/organization.service";
import { ActivatedRoute, Router } from "@angular/router";
import { OrganizationCreateConstants } from "../../constants/organization-create.constants";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { OrganizationUpdateRequest } from "../../dto/organization-update-request.dto";

@Component({
    selector: 'app-organization-update',
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

    constructor(
        private organizationService: OrganizationService,
        private router: Router,
        private route: ActivatedRoute

    ) { }

    ngOnInit(): void {
        //get the id
        this.organizationId = this.route.snapshot.paramMap.get('organizationId');

        if (this.organizationId) {
            this.loadOrganization(this.organizationId);
        } else {
            this.error.set('Organization ID is missing.');
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
                error: (error) => {
                    console.error(
                        'Failed to load organization',
                        error
                    );
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
                },
                error: (error) => {
                    console.error('Failed to update organization', error);
                    this.error.set('Unable to load organization');
                }
            })

    }
}
