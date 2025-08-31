import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from './auth.service';

@Component({
  selector: 'app-login',
  template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-body">
              <h2 class="card-title text-center mb-4">Search Engine Login</h2>
              <form (ngSubmit)="onSubmit()" #loginForm="ngForm">
                <div class="mb-3">
                  <label for="username" class="form-label">Username</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="username" 
                    name="username"
                    [(ngModel)]="credentials.username" 
                    required>
                </div>
                <div class="mb-3">
                  <label for="password" class="form-label">Password</label>
                  <input 
                    type="password" 
                    class="form-control" 
                    id="password" 
                    name="password"
                    [(ngModel)]="credentials.password" 
                    required>
                </div>
                <div class="d-grid">
                  <button 
                    type="submit" 
                    class="btn btn-primary" 
                    [disabled]="!loginForm.form.valid || isLoading">
                    <span *ngIf="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                    <i class="fas fa-sign-in-alt me-2" *ngIf="!isLoading"></i>
                    {{ isLoading ? 'Logging in...' : 'Login' }}
                  </button>
                </div>
              </form>
              <div *ngIf="errorMessage" class="alert alert-danger mt-3">
                {{ errorMessage }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  credentials: LoginRequest = {
    username: '',
    password: ''
  };
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onSubmit() {
    if (this.credentials.username && this.credentials.password) {
      this.isLoading = true;
      this.errorMessage = '';

      this.authService.login(this.credentials).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Login failed. Please check your credentials.';
          console.error('Login error:', error);
        }
      });
    }
  }
}