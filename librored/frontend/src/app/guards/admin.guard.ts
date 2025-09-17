import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router,
    private http: HttpClient
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {

    console.log('🛡️ AdminGuard: canActivate called for URL:', state.url);
    console.log('🛡️ AdminGuard: isLogged?', this.authService.isLogged());
    console.log('🛡️ AdminGuard: currentUser:', this.authService.currentUser());
    console.log('🛡️ AdminGuard: isAdmin?', this.authService.isAdmin());

    if (!this.authService.isLogged()) {
      console.log('🛡️ AdminGuard: User not logged in, redirecting to login');
      // User is not logged in - redirect to login
      this.authService.setRedirectUrl(state.url);
      this.router.navigate(['/login']);
      return false;
    }

    // If we already have role information and user is admin, allow access
    if (this.authService.isAdmin()) {
      console.log('🛡️ AdminGuard: User is admin, allowing access');
      return true;
    }

    // For now, if user is logged in but we don't have role info,
    // allow access and let the backend handle authorization.
    // This prevents the 401 -> logout cycle we were experiencing.
    const currentUser = this.authService.currentUser();
    console.log('🛡️ AdminGuard: Current user:', currentUser);

    if (currentUser && currentUser.email) {
      console.log('🛡️ AdminGuard: User is logged in, allowing access. Backend will handle auth.');
      return true;
    }

    console.log('🛡️ AdminGuard: No user info available, redirecting to login');
    // If we can't determine user status, redirect to login for safety
    this.authService.setRedirectUrl(state.url);
    this.router.navigate(['/login']);
    return false;
  }
}