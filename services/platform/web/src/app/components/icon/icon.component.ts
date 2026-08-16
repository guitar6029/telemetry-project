import { Component, input } from "@angular/core";
import { IconName } from "./icon.enums";
import { TrashIconComponent } from "./svg/trash.component";
import { EditIconComponent } from "./svg/edit.component";
import { ChevronLeftIconComponent } from "./svg/chevronLeft.component";
import { ChevronRightIconComponent } from "./svg/chevronRight.component";
import { NgComponentOutlet } from "@angular/common";
import { AddUserIconComponent } from "./svg/addUser.component";
import { PlusIconComponent } from "./svg/plus.component";

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
        [IconName.PLUS]: PlusIconComponent
    };

    name = input.required<IconName>();
}
