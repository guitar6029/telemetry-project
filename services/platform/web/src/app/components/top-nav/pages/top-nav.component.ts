

import { Component } from "@angular/core";
import { ProfileMenuComponent } from "../../profile/pages/profile-menu.component";
import { OrganizationSelectionComponent } from "../../organization-selection/pages/organization-selection.component";


@Component({
    selector: 'telemetry-top-nav',
    templateUrl: './top-nav.component.html',
    styleUrl: './top-nav.component.scss',
    imports: [ProfileMenuComponent, OrganizationSelectionComponent]
})

export class TopNavComponent {

}
