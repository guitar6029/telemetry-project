import { Component, input } from "@angular/core";
import { IconName } from "./icon.enums";
import { TrashIconComponent } from "./svg/trash.component";
import { EditIconComponent } from "./svg/edit.component";
import { ChevronLeftIconComponent } from "./svg/chevron-left.component";
import { ChevronRightIconComponent } from "./svg/chevron-right.component";
import { NgComponentOutlet } from "@angular/common";
import { AddUserIconComponent } from "./svg/add-user.component";
import { PlusIconComponent } from "./svg/plus.component";
import { SearchIconComponent } from "./svg/search.component";
import { ChevronUpIconComponent } from "./svg/chevron-up.component";
import { ChevronDownIconComponent } from "./svg/chevron-down.component";

@Component({
    selector: 'telemetry-icon',
    imports: [NgComponentOutlet],
    templateUrl: './icon.component.html',
})

export class IconComponent {

    protected readonly IconMap = {
        [IconName.TRASH]: TrashIconComponent,
        [IconName.EDIT]: EditIconComponent,
        [IconName.ARROW_LEFT]: ChevronLeftIconComponent,
        [IconName.ARROW_RIGHT]: ChevronRightIconComponent,
        [IconName.ADD_USER]: AddUserIconComponent,
        [IconName.PLUS]: PlusIconComponent,
        [IconName.SEARCH]: SearchIconComponent,
        [IconName.CHEVRON_UP]: ChevronUpIconComponent,
        [IconName.CHEVRON_DOWN]: ChevronDownIconComponent
    };

    name = input.required<IconName>();
}
