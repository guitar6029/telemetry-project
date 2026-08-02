import { computed, inject, Injectable, signal } from "@angular/core";
import { SessionConstants } from "../constants/session.constants";
import { Observable, tap } from "rxjs";
import { ApiResponse } from "../../../common/dto/api-response.dto";
import { MeResponse } from "../../profile/dto/me-response.dto";
import { ProfileService } from "../../profile/service/profile.service";
import { ProfileStore } from "../../../core/stores/profile.store";

@Injectable({
    providedIn: 'root'
})

export class SessionService {

    private readonly sessionStartedAt = signal<number | null>(null);
    private readonly profileService = inject(ProfileService);
    private readonly profileStore = inject(ProfileStore);

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
                this.profileStore.setProfile(response.data);
            }),

            tap(() => {
                this.startIdleTimer()
            })
        )
    }

    logout(): void {
        this.clearSession();
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
}
