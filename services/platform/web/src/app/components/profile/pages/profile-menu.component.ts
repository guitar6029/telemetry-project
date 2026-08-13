import { Component, inject } from "@angular/core";
import { ProfileStore } from "../../../core/stores/profile.store";
import { SessionService } from "../../../features/auth/service/session.service";
import { RouterLink } from "@angular/router";


@Component({
    selector: 'telemetry-profile-menu',
    templateUrl: './profile-menu.component.html',
    styleUrl: './profile-menu.component.scss',
    imports: [RouterLink],
})

export class ProfileMenuComponent {

    private readonly profileStore = inject(ProfileStore);
    private readonly sessionService = inject(SessionService);
    readonly fullName = this.profileStore.fullName;
    readonly email = this.profileStore.email;
    readonly avatarUrl = this.profileStore.avatarUrl;

    logout(): void {
        this.sessionService.logout();
    }
}
