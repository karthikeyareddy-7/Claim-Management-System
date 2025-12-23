import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root', // this must exist
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet></router-outlet>` // app root shows routed components
})
export class App {}


