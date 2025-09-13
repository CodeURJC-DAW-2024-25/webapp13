import { Component } from "@angular/core";
import { AuthService } from './services/auth.service';

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
})
export class AppComponent {
  public isCollapsed = true;

  constructor(private authService: AuthService) {}

  /**
   * Check if user is logged in
   */
  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  /**
   * Handle logout from main navigation
   */
  onLogout(): void {
    this.authService.logout().subscribe({
      next: () => {
        console.log('Logout successful');
        this.authService.setLoggedOut();
      },
      error: (error) => {
        console.error('Logout error:', error);
        this.authService.setLoggedOut(); // Clear state even if server logout fails
      }
    });
  }
}