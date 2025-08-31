import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AuthService, LoginRequest } from '../../auth/auth.service';

@Component({
  selector: 'app-login-modal',
  templateUrl: './login-modal.component.html',
  styleUrls: ['./login-modal.component.scss']
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