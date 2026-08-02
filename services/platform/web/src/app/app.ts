import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { SessionService } from './features/auth/service/session.service';

@Component({
  selector: 'telemetry-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
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
