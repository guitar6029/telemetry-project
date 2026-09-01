import { Component, output } from "@angular/core";
import { PageComponent } from "../../../../../components/page/page.component";
import { MAX_IMPORT_FILE_SIZE, MAX_IMPORT_NUMBER_OF_ROWS } from "../../constants/device-import.constants";

@Component({
    selector: 'telemetry-file-import',
    templateUrl: './file-import.component.html',
    imports: [PageComponent]
})

export class FileImportComponent {
    protected readonly MAX_FILE_SIZE = MAX_IMPORT_FILE_SIZE;
    protected readonly MAX_ROWS = MAX_IMPORT_NUMBER_OF_ROWS;

    fileSelected = output<File>();

    handleFileChange(event: Event): void {
        const input = event.target as HTMLInputElement;
        const file = input.files?.[0];

        if (!file) {
            return;
        }

        this.fileSelected.emit(file);
    }
}
