import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = '/api/auth';
  private loggedIn = false;
  private currentUser: any = null;
  private redirectUrl: string = '/books'; // Default redirect after login

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Check for stored authentication state on service initialization
    this.loadAuthState();
  }

  /**
   * Login with email and password
   */
  login(email: string, password: string): Observable<any> {
    const loginData = { username: email, password: password };
    return this.http.post(`${this.API_URL}/login`, loginData, { withCredentials: true });
  }

  /**
   * Logout current user
   */
  logout(): Observable<any> {
    return this.http.post(`${this.API_URL}/logout`, {}, { withCredentials: true });
  }

  /**
   * Check if user is currently logged in
   */
  isLoggedIn(): boolean {
    return this.loggedIn;
  }

  /**
   * Get current user information
   */
  getCurrentUser(): any {
    return this.currentUser;
  }

  /**
   * Clear login status (called after logout)
   */
  setLoggedOut(): void {
    this.loggedIn = false;
    this.currentUser = null;
    this.clearAuthState();
    this.router.navigate(['/']);
  }

  /**
   * Set the URL to redirect to after successful login
   */
  setRedirectUrl(url: string): void {
    this.redirectUrl = url;
  }

  /**
   * Get the URL to redirect to after login
   */
  getRedirectUrl(): string {
    return this.redirectUrl;
  }

  /**
   * Navigate to the stored redirect URL after successful login
   */
  redirectAfterLogin(): void {
    const url = this.redirectUrl;
    this.redirectUrl = '/books'; // Reset to default
    this.router.navigate([url]);
  }

  /**
   * Load authentication state from localStorage (for persistence across page refreshes)
   */
  private loadAuthState(): void {
    const stored = localStorage.getItem('authState');
    if (stored) {
      const authState = JSON.parse(stored);
      this.loggedIn = authState.loggedIn;
      this.currentUser = authState.currentUser;
    }
  }

  /**
   * Save authentication state to localStorage
   */
  private saveAuthState(): void {
    const authState = {
      loggedIn: this.loggedIn,
      currentUser: this.currentUser
    };
    localStorage.setItem('authState', JSON.stringify(authState));
  }

  /**
   * Clear authentication state from localStorage
   */
  private clearAuthState(): void {
    localStorage.removeItem('authState');
  }

  /**
   * Enhanced setLoggedIn with persistence
   */
  setLoggedIn(user: any = null): void {
    this.loggedIn = true;
    this.currentUser = user || { email: 'user@example.com', name: 'User' };
    this.saveAuthState();
  }
}