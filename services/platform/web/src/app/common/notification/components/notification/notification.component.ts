import { Component, inject } from "@angular/core";
import { NotificationService } from "../../service/notification.service";

@Component({
    selector: 'telemetry-notification',
    templateUrl: './notification.component.html'
})
export class NotificationComponent {
    private readonly notificationService = inject(NotificationService);

    readonly notification = this.notificationService.current;
    readonly position = this.notificationService.position;
    readonly count = this.notificationService.count;
    readonly canPrevious = this.notificationService.canPrevious;
    readonly canNext = this.notificationService.canNext;

    previous(): void {
        this.notificationService.previous();
    }

    next(): void {
        this.notificationService.next();
    }

    dismiss(): void {
        this.notificationService.clearPod();
    }
}
