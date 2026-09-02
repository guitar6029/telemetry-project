import { Component, output, signal } from "@angular/core";
import { PageComponent } from "../../../../../components/page/page.component";
import { MAX_IMPORT_FILE_SIZE, MAX_IMPORT_NUMBER_OF_ROWS } from "../../constants/device-import.constants";
import { DeviceImportMode } from "../../enums/device-import-mode.enums";

@Component({
    selector: 'telemetry-file-import',
    templateUrl: './file-import.component.html',
    imports: [PageComponent]
})

export class FileImportComponent {
    protected readonly MAX_FILE_SIZE = MAX_IMPORT_FILE_SIZE;
    protected readonly MAX_ROWS = MAX_IMPORT_NUMBER_OF_ROWS;

    fileSelected = output<File>();

    selectedImportMode = signal<DeviceImportMode>(
        DeviceImportMode.SKIP_EXISTING
    );

    // option
    protected readonly DeviceImportMode = DeviceImportMode;
    importModeSelected = output<DeviceImportMode>();

    handleFileChange(event: Event): void {
        const input = event.target as HTMLInputElement;
        const file = input.files?.[0];

        if (!file) {
            return;
        }

        this.fileSelected.emit(file);
    }

    handleImportModeChange(event: Event): void {
        const input = event.target as HTMLInputElement;

        this.importModeSelected.emit(
            input.checked
                ? DeviceImportMode.UPDATE_EXISTING
                : DeviceImportMode.SKIP_EXISTING
        );
    }


}
