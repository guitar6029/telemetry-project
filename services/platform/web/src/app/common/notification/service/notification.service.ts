import { Injectable, inject } from "@angular/core";
import { NotificationSettingsConstants } from "../constants/notification-settings.constants";
import { NotificationSettings } from "../types/notification.types";

@Injectable({
    providedIn: 'root'
})


export class NotificationService {



    private open(
        {
            message,
            duration = NotificationSettingsConstants.duration,
            horizontalPosition = 'right',
            verticalPosition = 'top'
        }: { message: string } & Partial<Omit<NotificationSettings, 'message'>>
    ) {
        console.log("notification")
        // this.snackBar.open(
        //     message,
        //     'Close',
        //     {
        //         duration,
        //         horizontalPosition,
        //         verticalPosition
        //     }
        // );
    }

    success(settings: Partial<NotificationSettings> = {}) {
        console.log("notification")
        // this.open({
        //     ...settings,
        //     message: settings.message ?? NotificationSettingsConstants.successMessage

        // });
    }

    error(settings: Partial<NotificationSettings> = {}) {
        console.log("notification")
        // this.open({
        //     ...settings,
        //     message: settings.message ?? NotificationSettingsConstants.errorMessage
        // });
    }

    warning(settings: Partial<NotificationSettings> = {}) {
        console.log("notification")
        // this.open({
        //     ...settings,
        //     message: settings.message ?? NotificationSettingsConstants.warningMessage
        // })
    }

    info(settings: Partial<NotificationSettings> = {}) {
        console.log("notification")
        // this.open({
        //     ...settings,
        //     message: settings.message ?? NotificationSettingsConstants.infoMessage
        // })
    }

}

