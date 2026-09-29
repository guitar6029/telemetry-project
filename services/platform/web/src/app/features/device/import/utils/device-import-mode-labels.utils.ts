import { DeviceImportMode } from "../enums/device-import-mode.enums";

export const DEVICE_IMPORT_MODE_LABELS: Record<DeviceImportMode, string> = {
    [DeviceImportMode.SKIP_EXISTING]: 'Skip Existing',
    [DeviceImportMode.UPDATE_EXISTING]: 'Update Existing'
};
