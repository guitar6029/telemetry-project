import { Component, computed, inject, signal } from "@angular/core";
import { DeviceImportStep } from "../types/device-import-steps.types";
import { DeviceImportMode } from "../enums/device-import-mode.enums";
import { PageComponent } from "../../../../components/page/page.component";
import { DEVICE_IMPORT_STEP_LABELS, DEVICE_IMPORT_STEPS } from "../constants/device-import.constants";
import { ButtonComponent } from "../../../../components/button/button.component";
import { Router } from "@angular/router";
import { DeviceTemplateSelectionComponent } from "../steps/device-template-selection/pages/device-template-selection.component";


@Component({
    selector: 'telemetry-devices-import',
    templateUrl: './devices-import.component.html',
    imports: [PageComponent, ButtonComponent, DeviceTemplateSelectionComponent]
})

export class DevicesImportComponent {

    readonly deviceImportSteps = DEVICE_IMPORT_STEPS;
    readonly stepLabels = DEVICE_IMPORT_STEP_LABELS;
    private readonly router = inject(Router);


    currentStep = signal<DeviceImportStep>(1);

    selectedTemplateId = signal<string | null>(null);
    selectedHierarchyNodeId = signal<string | null>(null);
    selectedFile = signal<File | null>(null);
    importMode = signal<DeviceImportMode>(DeviceImportMode.SKIP_EXISTING);

    readonly showCancel = computed(() => this.currentStep() === 1);

    readonly showPrevious = computed(() => this.currentStep() > 1);

    readonly canGoNext = computed(() => {
        switch (this.currentStep()) {
            case 1:
                return this.selectedTemplateId() !== null;

            case 2:
                return this.selectedHierarchyNodeId() !== null;

            case 3:
                return this.selectedFile() !== null;

            case 4:
                return true;
        }
    });

    cancelImport(): void {
        this.router.navigate(
            ['/app/devices']
        );
    }

    previousStep(): void {
        this.currentStep.update(step => (step - 1) as DeviceImportStep);
    }

    nextStep(): void {
        switch (this.currentStep()) {
            case 1:
                if (this.selectedTemplateId() === null) {
                    return;
                }

                this.currentStep.set(2);
                break;

            case 2:
                if (this.selectedHierarchyNodeId() === null) {
                    return;
                }

                this.currentStep.set(3);
                break;

            case 3:
                if (this.selectedFile() === null) {
                    return;
                }

                this.currentStep.set(4);
                break;

            case 4:
                // Submit import
                break;
        }
    }

    selectedDeviceTemplateUpdate(deviceTemplateId: string) {
        this.selectedTemplateId.set(deviceTemplateId);
    }
}
