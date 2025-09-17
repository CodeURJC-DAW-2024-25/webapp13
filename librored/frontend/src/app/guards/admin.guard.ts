import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {

    if (!this.authService.isLogged()) {
      // User is not logged in - redirect to login
      this.authService.setRedirectUrl(state.url);
      this.router.navigate(['/login']);
      return false;
    }

    // For now, if we don't have role info, assume the user might be admin
    // In a real app, you might want to fetch user details here
    // For simplicity, we'll allow access and let the backend reject if needed
    if (this.authService.isAdmin()) {
      return true; // User is admin
    }

    // If we can't determine admin status, redirect to books for safety
    // TODO: In future, could fetch user details to check role
    this.router.navigate(['/books']);
    return false;
  }
}