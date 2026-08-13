import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";


@Component({
    selector: 'telemetry-auth-layout',
    imports: [
        RouterOutlet,
    ],
    templateUrl: './auth-layout.component.html',
    styleUrl: './auth-layout.component.scss'
})

export class AuthLayoutComponent { }
