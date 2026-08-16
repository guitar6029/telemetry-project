import { ButtonStyle } from "./types/button-style.types";
import { ButtonType } from "./types/button-type.types";
import { Component, input, output } from "@angular/core";
import { IconComponent } from "../icon/icon.component";
import { IconName } from "../icon/icon.enums";
import { LoadingSpinnerComponent } from "../loading/loading-spinner/loading-spinner.component";
import { RouterLink } from "@angular/router";
import { SpinnerSize } from "../loading/loading-spinner/enums/spinner-size.enums";

@Component({
    selector: 'telemetry-button',
    templateUrl: './button.component.html',
    imports: [RouterLink, IconComponent, LoadingSpinnerComponent]
})

export class ButtonComponent {
    protected readonly ButtonType = ButtonType;
    protected readonly ButtonStyle = ButtonStyle;
    protected readonly LoadingSpinnerSize = SpinnerSize;
    text = input.required<string>();
    type = input<ButtonType>(ButtonType.BUTTON);
    disabled = input(false);
    style = input<ButtonStyle>(ButtonStyle.PRIMARY);
    clicked = output<MouseEvent>();
    routerLink = input<string | string[] | null>(null);
    icon = input<IconName>();
    loading = input(false);
}
