import { Component, input } from "@angular/core";

@Component({
    selector: 'telemetry-empty-state',
    templateUrl: './empty-state.component.html',
    styleUrl: './empty-state.component.scss'
})

export class EmptyStateComponent {
    message = input("No data found")
}
