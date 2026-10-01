import { Component, inject } from "@angular/core";
import { NotificationService } from "../../service/notification.service";

@Component({
    selector: 'telemetry-notification',
    templateUrl: './notification.component.html',
    styleUrl: './notification.component.scss'
})
export class NotificationComponent {
    private readonly notificationService = inject(NotificationService);

    readonly notification = this.notificationService.current;

    dismiss(): void {
        this.notificationService.clearAll();
    }
}
