import { Component, signal } from "@angular/core";
import { DeviceImportStep } from "../types/device-import-steps.types";
import { DeviceImportMode } from "../enums/device-import-mode.enums";

@Component({
    selector: 'telemetry-devices-import',
    templateUrl: './devices-import.component.html'
})

export class DevicesImportComponent {

    currentStep = signal<DeviceImportStep>(1);

    selectedTemplateId = signal<string | null>(null);
    selectedHierarchyNodeId = signal<string | null>(null);
    selectedFile = signal<File | null>(null);
    importMode = signal<DeviceImportMode>(DeviceImportMode.SKIP_EXISTING);


}
