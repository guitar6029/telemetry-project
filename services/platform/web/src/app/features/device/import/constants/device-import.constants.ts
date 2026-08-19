import { DeviceImportStep } from "../types/device-import-steps.types";

export const DEVICE_IMPORT_STEPS: DeviceImportStep[] = [
    1,
    2,
    3,
    4
];

export const DEVICE_IMPORT_STEP_LABELS: Record<DeviceImportStep, string> = {
    1: 'Device Template',
    2: 'Hierarchy Node',
    3: 'CSV File',
    4: 'Review'
};
