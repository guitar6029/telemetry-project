import { Routes } from "@angular/router";
import { DeviceImportDetailComponent } from "./device-imports/pages/device-import-detail.component";
import { DeviceImportListComponent } from "./device-imports/pages/device-import-list.component";

export const REPORTS_ROUTES: Routes = [
    {
        path: 'device-imports',
        children: [
            {
                path: '',
                component: DeviceImportListComponent
            },
            {
                path: ':importId',
                component: DeviceImportDetailComponent
            }
        ]
    }
];
