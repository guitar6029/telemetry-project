import { Component, input } from "@angular/core";

@Component({
    selector: 'telemetry-page-header',
    templateUrl: './page-header.component.html'
})

export class PageHeaderComponent {
    headerTitle = input.required<string>();
    description = input<string | null>(null)
}
