import { Component } from "@angular/core";
import { MatCardModule } from "@angular/material/card";
import { RouterOutlet } from "@angular/router";


@Component({
    selector: 'telemetry-auth-layout',
    imports: [
        RouterOutlet,
        MatCardModule
    ],
    templateUrl: './auth-layout.component.html',
    styleUrl: './auth-layout.component.scss'
})

export class AuthLayoutComponent { }
