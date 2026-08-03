import { computed, inject, Injectable, signal } from "@angular/core";
import { SessionConstants } from "../constants/session.constants";
import { catchError, EMPTY, Observable, tap, throwError } from "rxjs";
import { ApiResponse } from "../../../common/dto/api-response.dto";
import { MeResponse } from "../../profile/dto/me-response.dto";
import { ProfileService } from "../../profile/service/profile.service";
import { ProfileStore } from "../../../core/stores/profile.store";
import { InvalidSessionError } from "../exception/invalid-session.exception";
import { AuthService } from "./auth.service";
import { Router } from "@angular/router";
import { NotificationService } from "../../../common/notification/service/notification.service";
import { MessageDefaultConstants } from "../../../constants/message.constants";
import { SessionStatus } from "../types/session-status.types";

@Injectable({
    providedIn: 'root'
})

export class SessionService {

    private readonly sessionStartedAt = signal<number | null>(null);
    private readonly profileService = inject(ProfileService);
    private readonly profileStore = inject(ProfileStore);
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router)
    private readonly notificationService = inject(NotificationService);


    private readonly sessionStatus = signal<SessionStatus>("unknown");
    readonly sessionStatusReadOnly = this.sessionStatus.asReadonly();



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

        this.setUnknownStatus();

        return this.profileService.me().pipe(

            tap((response) => {
                this.validateProfile(response.data);
                this.profileStore.setProfile(response.data);
                this.setAuthenticatedStatus();
            }),

            tap(() => {
                this.startIdleTimer()
            }),

            catchError(() => {

                this.profileStore.clear();
                this.clearSession();

                this.setUnauthenticatedStatus();
                return EMPTY
            })
        )
    }

    logout(): void {
        this.authService.logout().subscribe({
            next: () => {
                this.profileStore.clear();
                this.clearSession();
                this.setUnauthenticatedStatus();

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


    private startIdleTimer(): void {
        this.sessionStartedAt.set(Date.now());
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


    private clearSession(): void {
        this.sessionStartedAt.set(null)
    }

    readonly isAuthenticated = computed(() =>
        this.sessionStatus() === 'authenticated'
    );

    readonly isUnauthenticated = computed(() =>
        this.sessionStatus() === 'unauthenticated'
    );

    readonly isInitializing = computed(() =>
        this.sessionStatus() === 'unknown'
    );

    private setUnknownStatus(): void {
        this.sessionStatus.set('unknown');
    }

    private setAuthenticatedStatus(): void {
        this.sessionStatus.set('authenticated');
    }

    private setUnauthenticatedStatus(): void {
        this.sessionStatus.set('unauthenticated');
    }
}
