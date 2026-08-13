import { Component, input } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { InputType } from "./types/input-type.types";

@Component({
    selector: 'telemetry-input',
    imports: [ReactiveFormsModule],
    templateUrl: './input.component.html'
})
export class InputComponent {

    protected readonly InputType = InputType;

    type = input<InputType>(InputType.TEXT);
    control = input.required<FormControl<string>>();
    placeholder = input<string | null>('');
}
