import { computed, Injectable, signal } from "@angular/core";
import { SessionConstants } from "../constants/session.constants";

@Injectable({
    providedIn: 'root'
})

export class SessionService {

    private readonly sessionStartedAt = signal<number | null>(null);

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

    initialize(): void {
        this.startIdleTimer()
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
