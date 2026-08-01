import { CanActivateFn, Router } from "@angular/router";
import { ProfileStore } from "../stores/profile.store";
import { inject } from "@angular/core";


export const authGuard: CanActivateFn = () => {
    const profileStore = inject(ProfileStore);
    const router = inject(Router);
    if (profileStore.profile() !== null) {
        return true;
    }

    return router.createUrlTree(['/auth/login'])
}
