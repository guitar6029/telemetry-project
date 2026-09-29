import { Injectable, computed, signal } from "@angular/core";
import { NotificationSettingsConstants } from "../constants/notification-settings.constants";
import { Notification, NotificationType } from "../types/notification.types";

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private readonly podSize = 5;
    private readonly notifications = signal<Notification[]>([]);
    private readonly currentIndex = signal(0);

    readonly current = computed(() => {
        const items = this.notifications();
        return items[this.currentIndex()] ?? null;
    });

    readonly count = computed(() => {
        const items = this.notifications();
        const currentPodStart = Math.floor(this.currentIndex() / this.podSize) * this.podSize;

        return Math.min(this.podSize, Math.max(items.length - currentPodStart, 0));
    });

    readonly position = computed(() => {
        if (!this.current()) {
            return 0;
        }

        return (this.currentIndex() % this.podSize) + 1;
    });

    readonly canPrevious = computed(() => this.currentIndex() % this.podSize > 0);

    readonly canNext = computed(() => {
        const index = this.currentIndex();
        return index < this.notifications().length - 1 &&
            index % this.podSize < this.podSize - 1;
    });

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

    next() {
        if (!this.canNext()) {
            return;
        }

        this.currentIndex.update(index => index + 1);
    }

    previous() {
        if (!this.canPrevious()) {
            return;
        }

        this.currentIndex.update(index => index - 1);
    }

    clearPod() {
        const items = this.notifications();
        if (items.length === 0) {
            return;
        }

        const podStart = Math.floor(this.currentIndex() / this.podSize) * this.podSize;
        const nextItems = items.slice(0, podStart).concat(items.slice(podStart + this.podSize));

        this.notifications.set(nextItems);
        this.currentIndex.set(Math.min(podStart, Math.max(nextItems.length - 1, 0)));
    }

    clearAll() {
        this.notifications.set([]);
        this.currentIndex.set(0);
    }

    private open(type: NotificationType, message: string) {
        this.notifications.update(items => [
            ...items,
            {
                id: crypto.randomUUID(),
                type,
                message,
                duration: NotificationSettingsConstants.duration
            }
        ]);
    }
}
