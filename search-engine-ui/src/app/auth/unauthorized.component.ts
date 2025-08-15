import { Component } from '@angular/core';
import { AuthService } from './auth.service';

@Component({
    selector: 'app-unauthorized',
    template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-8">
          <div class="alert alert-danger text-center">
            <h2><i class="fas fa-exclamation-triangle"></i> Access Denied</h2>
            <p class="mb-3">You don't have permission to access this resource.</p>
            <p class="mb-3">Admin privileges are required for indexing operations.</p>
            <button class="btn btn-primary me-2" routerLink="/">
              <i class="fas fa-home me-1"></i>
              Go Home
            </button>
            <button class="btn btn-secondary" (click)="logout()">
              <i class="fas fa-sign-out-alt me-1"></i>
              Logout
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class UnauthorizedComponent {

    constructor(private authService: AuthService) { }

    logout() {
        this.authService.logout();
    }
}