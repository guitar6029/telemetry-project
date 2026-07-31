import { Component, inject, OnInit, signal } from "@angular/core";
import { OrganizationService } from "../../service/organization.service";
import { ActivatedRoute } from "@angular/router";
import { OrganizationResponse } from "../../dto/organization-response.dto";
import { MatCardModule } from "@angular/material/card";
import { NotificationService } from "../../../../common/notification/service/notification.service";
import { MessageDefaultConstants } from "../../../../constants/message.constants";

@Component({
    selector: 'telemetry-organization-details',
    imports: [
        MatCardModule
    ],
    templateUrl: './organization-details.component.html',
    styleUrl: './organization-details.component.scss'
})

export class OrganizationDetailsComponent implements OnInit {

    organization = signal<OrganizationResponse | null>(null);
    error = signal<string | null>(null);


    private route = inject(ActivatedRoute)
    private organizationService = inject(OrganizationService)
    private notificationService = inject(NotificationService);


    ngOnInit(): void {
        const organizationId = this.route.snapshot.paramMap.get("organizationId");

        if (organizationId) {
            this.loadOrganization(organizationId);
        } else {
            this.error.set("Organization ID is missing.")
            this.notificationService.success({
                message: MessageDefaultConstants.organization.details.errorId
            });
        }

    }


    loadOrganization(organizationId: string): void {
        this.error.set(null);
        this.organizationService.getOrganization(organizationId).subscribe({
            next: (response) => {
                this.organization.set(response.data);
            },
            error: (httpError) => {
                this.error.set("Unable to load organization.");
                this.notificationService.error({
                    message: httpError.error?.message ?? MessageDefaultConstants.organization.details.error
                });
            }
        });

    }
}
