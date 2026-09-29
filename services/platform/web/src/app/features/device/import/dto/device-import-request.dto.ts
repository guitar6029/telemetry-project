import { DeviceImportMode } from "../enums/device-import-mode.enums";

export interface DeviceImport {
    deviceTemplateId: string;
    hierarchyNodeId: string;
    file: File;
    importMode: DeviceImportMode;
}
