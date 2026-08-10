

import { Component } from "@angular/core";
import { ProfileMenuComponent } from "../../profile/pages/profile-menu.component";


@Component({
    selector: 'telemetry-top-nav',
    templateUrl: './top-nav.component.html',
    styleUrl: './top-nav.component.scss',
    imports: [ProfileMenuComponent]
})

export class TopNavComponent {

}
