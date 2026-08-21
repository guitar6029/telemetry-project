import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";


@Component({
    selector: 'telemetry-main-content',
    templateUrl: './main-content.component.html',
    imports: [RouterOutlet],
    host: {
        class: 'flex min-h-0 flex-1 flex-col'
    }
})

export class MainContentComponent {

}
