import { Routes } from "@angular/router";
import { DevicesOverviewComponent } from "./devices-overview/pages/device-overview/devices-overview.component";
import { DevicesImportComponent } from "./import/pages/devices-import.component";

export const DEVICE_ROUTES: Routes = [
    {
        path: '',
        component: DevicesOverviewComponent

    },
    {
        path: 'import',
        component: DevicesImportComponent
    }
]
