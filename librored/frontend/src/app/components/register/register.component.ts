import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent {

  username: string = '';
  email: string = '';
  encodedPassword: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  showErrorModal: boolean = false;
  showSuccessModal: boolean = false;
  isSubmitting: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.username || !this.email || !this.encodedPassword || !this.confirmPassword) {
      this.showError('All fields are required.');
      return;
    }

    if (!this.isValidEmail(this.email)) {
      this.showError('Please enter a valid email address.');
      return;
    }

    if (this.encodedPassword !== this.confirmPassword) {
      this.showError('Passwords do not match.');
      return;
    }

    if (this.encodedPassword.length < 6) {
      this.showError('Password must be at least 6 characters long.');
      return;
    }

    this.isSubmitting = true;

    this.authService.register(this.username, this.email, this.encodedPassword).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        if (response.status === 'SUCCESS') {
          this.showSuccess('Registration successful! Please log in with your new account.');
        } else {
          this.showError(response.message || 'Registration failed.');
        }
      },
      error: (error) => {
        this.isSubmitting = false;
        if (error.error && error.error.message) {
          this.showError(error.error.message);
        } else if (error.status === 400) {
          this.showError('Registration failed. Please check your information and try again.');
        } else {
          this.showError('Registration failed. Please try again later.');
        }
      }
    });
  }

  private isValidEmail(email: string): boolean {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
  }

  private showError(message: string) {
    this.errorMessage = message;
    this.showErrorModal = true;
  }

  private showSuccess(message: string) {
    this.successMessage = message;
    this.showSuccessModal = true;
  }

  closeErrorModal() {
    this.showErrorModal = false;
  }

  closeSuccessModal() {
    this.showSuccessModal = false;
    this.router.navigate(['/login']);
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }

}