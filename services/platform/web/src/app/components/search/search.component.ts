import { Component, output, input } from "@angular/core";
import { SEARCH_DEBOUNCE_MS } from "./constants/search.constants";
import { SearchIconComponent } from "../icon/svg/search.component";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { debouncer } from "../../utils/debouncer";
import { distinctUntilChanged } from "rxjs";

@Component({
    selector: 'telemetry-search',
    templateUrl: './search.component.html',
    imports: [
        ReactiveFormsModule,
        SearchIconComponent
    ]
})

export class SearchComponent {
    placeholder = input('Search');
    debounceMs = input(SEARCH_DEBOUNCE_MS);

    searchChange = output<string>();

    searchControl = new FormControl('', {
        nonNullable: true
    });

    constructor() {
        this.searchControl.valueChanges
            .pipe(
                debouncer(this.debounceMs()),
                distinctUntilChanged()
            )
            .subscribe(value => {
                console.log("search :", value);
                this.searchChange.emit(value);

            }
            )
    }

}
