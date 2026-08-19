import { Component, signal } from "@angular/core";
import { DeviceImportStep } from "../types/device-import-steps.types";

@Component({
    selector: 'telemetry-devices-import',
    templateUrl: './devices-import.component.html'
})

export class DevicesImportComponent {

    currentStep = signal<DeviceImportStep>(1);
}
