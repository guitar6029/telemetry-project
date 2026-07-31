import { Injectable, inject } from "@angular/core";
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationSettingsConstants } from "../constants/notification-settings.constants";
import { NotificationSettings } from "../types/notification.types";

@Injectable({
    providedIn: 'root'
})


export class NotificationService {

    private readonly snackBar = inject(MatSnackBar);

    private open(
        {
            message,
            duration = NotificationSettingsConstants.duration,
            horizontalPosition = 'right',
            verticalPosition = 'top'
        }: { message: string } & Partial<Omit<NotificationSettings, 'message'>>
    ) {
        this.snackBar.open(
            message,
            'Close',
            {
                duration,
                horizontalPosition,
                verticalPosition
            }
        );
    }

    success(settings: Partial<NotificationSettings> = {}) {
        this.open({
            ...settings,
            message: settings.message ?? NotificationSettingsConstants.successMessage

        });
    }

    error(settings: Partial<NotificationSettings> = {}) {
        this.open({
            ...settings,
            message: settings.message ?? NotificationSettingsConstants.errorMessage
        });
    }

    warning(settings: Partial<NotificationSettings> = {}) {
        this.open({
            ...settings,
            message: settings.message ?? NotificationSettingsConstants.warningMessage
        })
    }

    info(settings: Partial<NotificationSettings> = {}) {
        this.open({
            ...settings,
            message: settings.message ?? NotificationSettingsConstants.infoMessage
        })
    }

}

