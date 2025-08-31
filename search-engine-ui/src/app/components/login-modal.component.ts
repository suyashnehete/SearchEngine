import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AuthService, LoginRequest } from '../auth/auth.service';

@Component({
    selector: 'app-login-modal',
    template: `
    <div class="modal fade" [class.show]="isVisible" [style.display]="isVisible ? 'block' : 'none'" 
         tabindex="-1" role="dialog" [attr.aria-hidden]="!isVisible">
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Sign In</h5>
            <button type="button" class="btn-close" (click)="close()" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form (ngSubmit)="onSubmit()" #loginForm="ngForm">
              <div class="mb-3">
                <label for="modalUsername" class="form-label">Username</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="modalUsername" 
                  name="username"
                  [(ngModel)]="credentials.username" 
                  required
                  autocomplete="username">
              </div>
              <div class="mb-3">
                <label for="modalPassword" class="form-label">Password</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="modalPassword" 
                  name="password"
                  [(ngModel)]="credentials.password" 
                  required
                  autocomplete="current-password">
              </div>
              <div class="d-grid gap-2">
                <button 
                  type="submit" 
                  class="btn btn-primary" 
                  [disabled]="!loginForm.form.valid || isLoading">
                  <span *ngIf="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                  <i class="fas fa-sign-in-alt me-2" *ngIf="!isLoading"></i>
                  {{ isLoading ? 'Signing in...' : 'Sign In' }}
                </button>
                <button type="button" class="btn btn-secondary" (click)="close()">
                  Cancel
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
    <div class="modal-backdrop fade" [class.show]="isVisible" *ngIf="isVisible"></div>
  `,
    styles: [`
    .modal {
      z-index: 1050;
    }
    .modal-backdrop {
      z-index: 1040;
    }
  `]
})
export class LoginModalComponent {
    @Input() isVisible = false;
    @Output() loginSuccess = new EventEmitter<void>();
    @Output() modalClosed = new EventEmitter<void>();

    credentials: LoginRequest = {
        username: '',
        password: ''
    };
    isLoading = false;
    errorMessage = '';

    constructor(private authService: AuthService) { }

    onSubmit() {
        if (this.credentials.username && this.credentials.password) {
            this.isLoading = true;
            this.errorMessage = '';

            this.authService.login(this.credentials).subscribe({
                next: () => {
                    this.isLoading = false;
                    this.resetForm();
                    this.loginSuccess.emit();
                    this.close();
                },
                error: (error) => {
                    this.isLoading = false;
                    this.errorMessage = 'Login failed. Please check your credentials.';
                    console.error('Login error:', error);
                }
            });
        }
    }

    close() {
        this.resetForm();
        this.modalClosed.emit();
    }

    private resetForm() {
        this.credentials = { username: '', password: '' };
        this.errorMessage = '';
        this.isLoading = false;
    }
}