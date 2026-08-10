import { Component, inject, signal } from "@angular/core";
import { ProfileStore } from "../../../core/stores/profile.store";
import { SessionService } from "../../../features/auth/service/session.service";


@Component({
    selector: 'telemetry-profile-menu',
    templateUrl: './profile-menu.component.html',
    styleUrl: './profile-menu.component.scss',
})

export class ProfileMenuComponent {

    private readonly profileStore = inject(ProfileStore);
    private readonly sessionService = inject(SessionService);
    readonly fullName = this.profileStore.fullName;
    readonly email = this.profileStore.email;
    readonly avatarUrl = this.profileStore.avatarUrl;

    dropdownDisplaying = signal(false)

    toggleDropdown(): void {
        this.dropdownDisplaying.set(!this.dropdownDisplaying());
    }

    logout(): void {
        this.sessionService.logout();
    }
}
