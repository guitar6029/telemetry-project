import { Injectable, signal } from "@angular/core";
import { NotificationSettingsConstants } from "../constants/notification-settings.constants";
import { Notification, NotificationType } from "../types/notification.types";

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private dismissTimeout: ReturnType<typeof setTimeout> | undefined;
    readonly current = signal<Notification | null>(null);

    success(message?: string) {
        this.open('success', message ?? NotificationSettingsConstants.successMessage);
    }

    error(message?: string) {
        this.open('error', message ?? NotificationSettingsConstants.errorMessage);
    }

    warning(message?: string) {
        this.open('warning', message ?? NotificationSettingsConstants.warningMessage);
    }

    info(message?: string) {
        this.open('info', message ?? NotificationSettingsConstants.infoMessage);
    }

    clearAll() {
        if (this.dismissTimeout) {
            clearTimeout(this.dismissTimeout);
            this.dismissTimeout = undefined;
        }

        this.current.set(null);
    }

    private open(type: NotificationType, message: string) {
        this.clearAll();

        const notification: Notification = {
            id: crypto.randomUUID(),
            type,
            message,
            duration: NotificationSettingsConstants.duration
        };

        this.current.set(notification);
        this.dismissTimeout = setTimeout(() => this.clearAll(), notification.duration);
    }
}
