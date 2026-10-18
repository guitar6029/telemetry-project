import { DeviceImportMode } from "../../../device/import/enums/device-import-mode.enums";
import { DeviceImportStatus } from "../../../device/import/enums/device-import-status.enums";

export interface DeviceImportResponse {
    importId: string;
    organizationId: string;
    templateId: string;
    hierarchyNodeId: string;
    filename: string;
    importMode: DeviceImportMode;
    status: DeviceImportStatus;
    submittedAt: string;
    startedAt: string | null;
    completedAt: string | null;
}
