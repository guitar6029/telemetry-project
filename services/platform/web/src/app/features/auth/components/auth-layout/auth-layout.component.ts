import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { MatCardModule } from "../../../../shared/material/material.imports";

@Component({
    selector: 'app-auth-layout',
    imports: [
        RouterOutlet,
        MatCardModule
    ],
    templateUrl: './auth-layout.component.html',
    styleUrl: './auth-layout.component.scss'
})

export class AuthLayoutComponent { }
