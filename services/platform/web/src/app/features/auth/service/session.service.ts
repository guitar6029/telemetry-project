import { computed, inject, Injectable, signal } from "@angular/core";
import { SessionConstants } from "../constants/session.constants";
import { Observable, tap } from "rxjs";
import { ApiResponse } from "../../../common/dto/api-response.dto";
import { MeResponse } from "../../profile/dto/me-response.dto";
import { ProfileService } from "../../profile/service/profile.service";
import { ProfileStore } from "../../../core/stores/profile.store";
import { InvalidSessionError } from "../exception/invalid-session.exception";
import { AuthService } from "./auth.service";
import { Router } from "@angular/router";
import { NotificationService } from "../../../common/notification/service/notification.service";
import { MessageDefaultConstants } from "../../../constants/message.constants";

@Injectable({
    providedIn: 'root'
})

export class SessionService {

    private readonly sessionStartedAt = signal<number | null>(null);
    private readonly profileService = inject(ProfileService);
    private readonly profileStore = inject(ProfileStore);
    private readonly authSession = inject(AuthService);
    private readonly router = inject(Router)
    private readonly notificationService = inject(NotificationService);

    readonly sessionMinutesRemaining = computed(() => {
        const start = this.sessionStartedAt();
        if (start === null) return 0;
        const passed = this.sessionMinutesPassed();

        const durationInMinutes =
            SessionConstants.SESSION_DURATION /
            (SessionConstants.MILLI_SECONDS_PER_MINUTE);

        return Math.max(0, durationInMinutes - passed)
    })

    readonly sessionMinutesPassed = computed(() => {
        const start = this.sessionStartedAt();
        if (start === null) return 0;
        const msPassed = Date.now() - start;

        return msPassed / (SessionConstants.MILLI_SECONDS_PER_MINUTE)
    })

    initialize(): Observable<ApiResponse<MeResponse>> {
        return this.profileService.me().pipe(

            tap((response) => {

                this.validateProfile(response.data);


                this.profileStore.setProfile(response.data);
            }),

            tap(() => {
                this.startIdleTimer()
            })
        )
    }

    logout(): void {
        this.authSession.logout().subscribe({
            next: () => {

                this.profileStore.clear();
                this.clearSession();

                this.router.navigate(
                    ['/auth/login'],
                    {
                        replaceUrl: true
                    }
                );
            },
            error: (httpError) => {
                this.notificationService.error({
                    message: httpError.error?.message ?? MessageDefaultConstants.auth.logout.error,
                });
            }
        })
    }

    startIdleTimer(): void {
        this.sessionStartedAt.set(Date.now());
    }

    resetIdleTimer(): void {
        this.startIdleTimer();
    }

    clearSession(): void {
        this.sessionStartedAt.set(null)
    }

    private validateProfile(profile: MeResponse): void {

        if (!profile.organizationId) {
            throw new InvalidSessionError(
                "Authenticated profile is missing organizationId."
            );
        }

        if (!profile.firstName) {
            throw new InvalidSessionError(
                "Authenticated profile is missing firstName."
            );
        }

        if (!profile.lastName) {
            throw new InvalidSessionError(
                "Authenticated profile is missing lastName."
            );
        }

        if (!profile.email) {
            throw new InvalidSessionError(
                "Authenticated profile is missing email."
            );
        }

        if (!profile.avatarUrl) {
            throw new InvalidSessionError(
                "Authenticated profile is missing avatarUrl."
            );
        }
    }
}
