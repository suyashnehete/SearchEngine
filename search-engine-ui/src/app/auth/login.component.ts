import { Component } from '@angular/core';
import { AuthService } from './auth.service';

@Component({
    selector: 'app-login',
    template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-body text-center">
              <h2 class="card-title mb-4">Search Engine Login</h2>
              <p class="card-text mb-4">Please login to access the search engine</p>
              <button class="btn btn-primary btn-lg" (click)="login()">
                <i class="fas fa-sign-in-alt me-2"></i>
                Login with OAuth2
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {

    constructor(private authService: AuthService) { }

    login() {
        this.authService.login();
    }
}