import { DeviceImportStatus } from "../enums/device-import-status.enums";

export interface DeviceImportResponse {
    message: string,
    deviceImportStatus: DeviceImportStatus
}
