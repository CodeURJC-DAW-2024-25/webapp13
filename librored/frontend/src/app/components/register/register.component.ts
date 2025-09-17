import { Component } from '@angular/core';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  // Simplified register component for now
  username = '';
  email = '';
  password = '';
  confirmPassword = '';
  encodedPassword = '';
  isSubmitting = false;
  showErrorModal = false;
  showSuccessModal = false;
  errorMessage = '';
  successMessage = '';

  register() {
    console.log('Register method called');
  }

  onSubmit() {
    this.register();
  }

  goToLogin() {
    console.log('Go to login');
  }

  closeErrorModal() {
    this.showErrorModal = false;
  }

  closeSuccessModal() {
    this.showSuccessModal = false;
  }
}