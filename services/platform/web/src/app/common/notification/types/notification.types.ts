type NotificationHorizontalPosition = 'left' | 'center' | 'right';
type NotificationVerticalPosition = 'top' | 'bottom';

export interface NotificationSettings {
    message: string;
    duration: number;
    horizontalPosition: NotificationHorizontalPosition;
    verticalPosition: NotificationVerticalPosition;
}
