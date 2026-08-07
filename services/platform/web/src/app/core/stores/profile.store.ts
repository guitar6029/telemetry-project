import { computed, Injectable, signal } from "@angular/core";
import { MeResponse } from "../../features/profile/dto/me-response.dto";
import { InvalidSessionError } from "../../features/auth/exception/invalid-session.exception";


@Injectable({
    providedIn: 'root'
})

export class ProfileStore {

    private readonly _profile = signal<MeResponse | null>(null)

    readonly profile = this._profile.asReadonly();

    private get profileOrThrow(): MeResponse {
        const profile = this._profile();

        if (profile === null) {
            throw new InvalidSessionError("Authenticated profile is not initialized.");
        }

        return profile;
    }

    setProfile(profile: MeResponse) {
        this._profile.set(profile);
    }

    clear(): void {
        this._profile.set(null);
    }

    readonly lastOrganizationUsed = computed(() =>
        this.profileOrThrow.lastOrganizationUsed
    );
}
