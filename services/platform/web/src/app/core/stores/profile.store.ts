import { computed, Injectable, signal } from "@angular/core";
import { MeResponse } from "../../features/profile/dto/me-response.dto";


@Injectable({
    providedIn: 'root'
})

export class ProfileStore {

    private readonly _profile = signal<MeResponse | null>(null)

    readonly profile = this._profile.asReadonly();

    readonly organizationId = computed(() =>
        this._profile()?.organizationId ?? null
    );

    requireOrganizationId(): string {

        const organizationId = this.organizationId();

        if (organizationId === null) {
            throw new Error("Organization context is not initialized.");
        }

        return organizationId;
    }


    setProfile(response: MeResponse) {
        this._profile.set(response);
    }

    getFullName() {
        return `${this._profile()?.firstName} ${this._profile()?.lastName}`
    }

    getEmail() {
        return this._profile()?.email
    }

    getAvatarUrl() {
        return this._profile()?.avatarUrl
    }

    getOrganizationId() {
        return this._profile()?.organizationId
    }
}
