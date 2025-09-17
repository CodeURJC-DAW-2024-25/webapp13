import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {

    if (this.authService.isLogged()) {
      return true; // Allow navigation to protected route
    } else {
      // Store the attempted URL for redirecting after login
      this.authService.setRedirectUrl(state.url);

      // Redirect to login page
      this.router.navigate(['/login']);
      return false; // Block navigation to protected route
    }
  }
}