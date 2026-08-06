import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SessionService } from './features/auth/service/session.service';

@Component({
  selector: 'telemetry-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  protected readonly title = signal('telemetry');
  protected readonly sessionService = inject(SessionService);

  ngOnInit(): void {
    const splash = document.getElementById('startup-loading');

    splash?.remove();
  }
}
