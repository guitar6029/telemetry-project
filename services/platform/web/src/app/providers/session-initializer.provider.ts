import { EnvironmentProviders, inject, provideAppInitializer } from "@angular/core";
import { SessionService } from "../features/auth/service/session.service";

export function provideSessionInitializer(): EnvironmentProviders {
    return provideAppInitializer(() => {
        const sessionService = inject(SessionService);
        return sessionService.initialize();
    });
}
