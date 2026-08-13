import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../auth/service/auth.service';
import { SessionService } from '../../../auth/service/session.service';


@Component({
    selector: 'telemetry-dashboard',
    // imports: [RouterLink],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {


    private readonly sessionService = inject(SessionService);

    logout() {
        this.sessionService.logout();
    }

}
