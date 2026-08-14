import { Component, input } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'telemetry-link-column',
    imports: [RouterLink],
    templateUrl: './link.component.html'
})

export class LinkColumnComponent {
    value = input.required<unknown>();
    routerLink = input.required<string | readonly unknown[]>();
}
