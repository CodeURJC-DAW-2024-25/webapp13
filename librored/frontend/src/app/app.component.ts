import { Component } from "@angular/core";
import { AuthService } from './services/auth.service';

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
})
export class AppComponent {
  public isCollapsed = true;

  constructor(public authService: AuthService) {}

  /**
   * Check if user is logged in
   */
  isLogged(): boolean {
    return this.authService.isLogged();
  }

  /**
   * Check if current user is admin
   */
  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  /**
   * Get current user
   */
  getCurrentUser() {
    return this.authService.currentUser();
  }
}