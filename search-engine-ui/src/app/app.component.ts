import { Component, OnInit } from '@angular/core';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'search-engine-ui';

  constructor(private authService: AuthService) { }

  ngOnInit() {
    // OAuth service is initialized in AuthService constructor
  }
}
