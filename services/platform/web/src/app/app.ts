import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { SessionService } from './features/auth/service/session.service';
import { TopNavComponent } from './components/top-nav/pages/top-nav.component';
import { SideNavComponent } from './components/side-nav/pages/side-nav.component';
import { MainContentComponent } from './components/main-content/pages/main-content.component';

@Component({
  selector: 'telemetry-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  protected readonly title = signal('telemetry');

  private readonly sessionService = inject(SessionService);
  private readonly router = inject(Router);

  ngOnInit(): void {

    this.sessionService.initialize().subscribe({
      error: () => {
        this.router.navigate(['/auth/login']);
      }
    });

  }
}
