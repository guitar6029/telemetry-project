import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';


@Component({
    selector: 'telemetry-dashboard',
    imports: [RouterLink],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.scss'
})
export class DashboardComponent { }
