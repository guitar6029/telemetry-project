import { Component, input, output } from "@angular/core";
import { SelectOption } from "./types/select-option.types";

@Component({
    selector: 'telemetry-select',
    templateUrl: './select.component.html'
})

export class SelectComponent {

    options = input.required<SelectOption[]>();
    value = input<string>('');
    valueChange = output<string>();
    label = input<string | null>();
    disabled = input(false);

    onChange(event: Event) {
        const value = (event.target as HTMLSelectElement).value;
        this.valueChange.emit(value);
    }
}
