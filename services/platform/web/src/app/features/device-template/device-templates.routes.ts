import { Routes } from "@angular/router";
import { DeviceTemplateFormComponent } from "./components/device-template-form/pages/device-template-form.component";
import { DeviceTemplatesOverviewComponent } from "./device-templates-overview/pages/device-templates-overview.component";

export const DEVICE_TEMPLATES_ROUTES: Routes = [
    {
        path: '',
        component: DeviceTemplatesOverviewComponent
    },
    {
        path: 'new',
        component: DeviceTemplateFormComponent
    },
    {
        path: ':deviceTemplateId/edit',
        component: DeviceTemplateFormComponent
    }
]
