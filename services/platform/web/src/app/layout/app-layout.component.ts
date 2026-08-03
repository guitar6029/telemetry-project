import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { TopNavComponent } from "../components/top-nav/pages/top-nav.component";
import { MainContentComponent } from "../components/main-content/pages/main-content.component";
import { SideNavComponent } from "../components/side-nav/pages/side-nav.component";

@Component({
    selector: 'telemetry-app-layout',
    imports: [TopNavComponent, MainContentComponent, SideNavComponent],
    templateUrl: './app-layout.component.html',
    styleUrl: './app-layout.component.scss'
})

export class AppLayoutComponent {

}
