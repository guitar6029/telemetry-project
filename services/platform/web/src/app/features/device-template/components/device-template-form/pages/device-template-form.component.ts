import { Component, inject, OnInit, signal } from "@angular/core";
import { DeviceTemplateService } from "../service/device-template.service";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { NotificationService } from "../../../../../common/notification/service/notification.service";
import { FormArray, FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { DeviceTemplateConstants } from "../../../constants/device-template.constants";
import { DeviceTemplateRequest } from "../../../dto/device-template-request.dto";
import { MetricDataType } from "../../../../metric-definition/enums/metric-data-type.enum";
import { MetricDefinitionConstants } from "../../../../metric-definition/constants/metric-definition.constants";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { MatSelectModule } from "@angular/material/select";
import { EmptyStateComponent } from "../../../../../common/components/empty-state/empty-state.component";
import { MetricDefinitionRequest } from "../../../../metric-definition/dto/metric-definition-request.dto";
import { Observable } from "rxjs";
import { ApiResponse } from "../../../../../common/dto/api-response.dto";
import { DeviceTemplateResponse } from "../../../dto/device-template-response.dto";
import { MetricDefinitionUpdateRequest } from "../../../../metric-definition/dto/metric-definition-update-request.dto";
import { DeviceTemplateUpdateRequest } from "../../../dto/device-template-update-request.dto";


@Component({
    selector: 'telemetry-device-template-create',
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
    templateUrl: './device-template-form.component.html',
    styleUrl: './device-template-form.component.scss'
})

export class DeviceTemplateFormComponent implements OnInit {

    private readonly route = inject(ActivatedRoute);
    private readonly deviceTemplateService = inject(DeviceTemplateService);
    private readonly router = inject(Router);
    private readonly notificationService = inject(NotificationService);

    readonly dataTypes = Object.values(MetricDataType);

    editMode = signal<boolean>(false);
    error = signal<string | null>(null);
    saving = signal(false);
    loading = signal(true);

    deviceTemplate = signal<DeviceTemplateResponse | null>(null);
    deviceTemplateId = signal<string | null>(null);


    ngOnInit(): void {
        const deviceTemplateId = this.route.snapshot.paramMap.get("deviceTemplateId")

        this.editMode.set(this.route.snapshot.routeConfig?.path === ':deviceTemplateId/edit');

        if (deviceTemplateId) {
            this.loadDeviceTemplate(deviceTemplateId);
        } else {
            this.loading.set(false)
        }
    }

    deviceTemplateForm = new FormGroup({
        name: new FormControl(
            { value: '', disabled: false },
            {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(DeviceTemplateConstants.NAME_MIN_LENGTH),
                    Validators.maxLength(DeviceTemplateConstants.NAME_MAX_LENGTH)
                ]
            }
        ),
        description: new FormControl(
            { value: '', disabled: false },
            {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(DeviceTemplateConstants.DESCRIPTION_MIN_LENGTH),
                    Validators.maxLength(DeviceTemplateConstants.DESCRIPTION_MAX_LENGTH)
                ]
            }
        ),

        metricDefinitions: new FormArray<FormGroup>([])

    })


    submitForm(): void {
        if (this.deviceTemplateForm.invalid) {
            return;
        }

        const { name, description, metricDefinitions } = this.deviceTemplateForm.getRawValue()

        if (this.editMode()) {
            const deviceTemplateId = this.deviceTemplateId();

            if (!deviceTemplateId) {
                return;
            }

            const request: DeviceTemplateUpdateRequest = {
                name,
                description,
                metricDefinitions: metricDefinitions as MetricDefinitionUpdateRequest[]
            }

            this.deviceTemplateService.updateDeviceTemplate(request, deviceTemplateId).subscribe({
                next: (response) => {

                    this.router.navigate(['/app/device-templates']);;
                    this.notificationService.success({
                        message: "Successfully updated a device template"
                    })

                },
                error: (httpError) => {
                    console.error(httpError);
                }
            })
        } else {

            const request: DeviceTemplateRequest = {
                name,
                description,
                metricDefinitions: metricDefinitions as MetricDefinitionRequest[]
            }

            this.deviceTemplateService.createDeviceTemplate(request).subscribe({
                next: (response) => {

                    this.router.navigate(['/device-templates']);
                    this.notificationService.success({
                        message: "Successfully created a device template"
                    })

                },
                error: (httpError) => {
                    console.error(httpError);
                }
            })
        }
    }



    loadDeviceTemplate(deviceTemplateId: string): void {
        this.loading.set(true);
        this.error.set(null);
        this.deviceTemplateService.getDeviceTemplate(deviceTemplateId).subscribe({
            next: (response) => {
                this.deviceTemplate.set(response.data)


                this.deviceTemplateForm.patchValue({
                    name: response.data.name,
                    description: response.data.description ?? ''
                });

                this.metricDefinitions.clear();

                response.data.metricDefinitions.forEach(metricDefinition => {
                    const form = this.createMetricDefinitionForm();

                    form.patchValue({
                        id: metricDefinition.id,
                        name: metricDefinition.name,
                        incomingFieldName: metricDefinition.incomingFieldName,
                        dataType: metricDefinition.dataType,
                        description: metricDefinition.description ?? '',
                        unit: metricDefinition.unit ?? ''
                    });

                    this.metricDefinitions.push(form);
                });

                this.deviceTemplateId.set(deviceTemplateId);

                this.loading.set(false);
            },
            error: (httpError) => {
                this.notificationService.error({
                    message: "Cannot load device template"
                })
                console.error(httpError);
                this.loading.set(false);
            }
        })
    }

    get metricDefinitions(): FormArray<FormGroup> {
        return this.deviceTemplateForm.controls.metricDefinitions;
    }

    addMetricDefinition(): void {
        this.metricDefinitions.push(
            this.createMetricDefinitionForm()
        );
    }

    removeMetricDefinition(index: number): void {
        this.metricDefinitions.removeAt(index);
    }



    private createMetricDefinitionForm(): FormGroup {
        return new FormGroup({
            id: new FormControl<string | null>(null),
            name: new FormControl('', {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(MetricDefinitionConstants.NAME_MIN_LENGTH),
                    Validators.maxLength(MetricDefinitionConstants.NAME_MAX_LENGTH)
                ]
            }),

            incomingFieldName: new FormControl('', {
                nonNullable: true,
                validators: [
                    Validators.required,
                    Validators.minLength(MetricDefinitionConstants.INCOMING_FIELD_NAME_MIN_LENGTH),
                    Validators.maxLength(MetricDefinitionConstants.INCOMING_FIELD_NAME_MAX_LENGTH)
                ]
            }),

            dataType: new FormControl(MetricDataType.STRING, {
                nonNullable: true,
                validators: [
                    Validators.required
                ]
            }),

            description: new FormControl('', {
                nonNullable: true
            }),

            unit: new FormControl('', {
                nonNullable: true
            })
        });
    }
}
