import { Component, input, output } from "@angular/core";
import { ButtonType } from "./types/button-type.types";
import { ButtonStyle } from "./types/button-style.types";

@Component({
    selector: 'telemetry-button',
    templateUrl: './button.component.html'
})

export class ButtonComponent {

    protected readonly ButtonType = ButtonType;
    protected readonly ButtonStyle = ButtonStyle;
    text = input.required<string>();
    type = input<ButtonType>(ButtonType.BUTTON);
    disabled = input(false);
    style = input<ButtonStyle>(ButtonStyle.PRIMARY);
    clicked = output<MouseEvent>();
}
