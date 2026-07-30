import { Component } from "@angular/core";
import { OrganizationService } from "../../service/organization.service";
import { Router } from "@angular/router";
import { OrganizationCreateConstants } from "../../constants/organization-create.constants";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { OrganizationCreateRequest } from "../../dto/organization-create-request.dto";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";

@Component({
    selector: 'app-organization-create',
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

    constructor(
        private organizationService: OrganizationService,
        private router: Router

    ) { }
    // future , we could implement a smart checker for already existing slug
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

        // pre check if slug already exists
        // or just send the request and see if 200
        const request: OrganizationCreateRequest = {
            name,
            slug
        }

        this.organizationService.createOrganization(request).subscribe({
            next: (response) => {
                this.router.navigate([`/organizations/${response.data.id}`])
            },
            error: (error) => {
                //noty error or form ui error
                console.error("Failed to create organization", error);
            }
        })

    }
}
