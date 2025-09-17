import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { of } from 'rxjs';

const BASE_URL = "/api/auth";

interface AuthResponse {
  status: 'SUCCESS' | 'FAILURE';
  message: string;
  error?: string;
}

interface LoginUser {
  username: string;
  role?: string; // We'll try to get this, but might not have it initially
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public logged: boolean = false;
  public user: LoginUser | undefined;

  // BehaviorSubject to notify components about auth state changes
  private authStateSubject = new BehaviorSubject<boolean>(false);
  public authState$ = this.authStateSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Don't try to check if logged in on startup
    // Instead, react to API responses
  }

  /**
   * Login with username and password
   * Works with your existing /api/auth/login endpoint
   */
  public logIn(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      BASE_URL + "/login",
      { username: username, password: password },
      { withCredentials: true }
    ).pipe(
      tap((response: AuthResponse) => {
        if (response.status === 'SUCCESS') {
          // Set user as logged in
          this.logged = true;
          this.user = {
            username: username
            // We don't know the role yet, but we can get it later if needed
          };
          this.authStateSubject.next(true);
        }
      }),
      catchError((error: HttpErrorResponse) => {
        this.logged = false;
        this.user = undefined;
        this.authStateSubject.next(false);
        throw error;
      })
    );
  }

  /**
   * Logout current user
   * Works with your existing /api/auth/logout endpoint
   */
  public logOut(): void {
    this.http.post<AuthResponse>(BASE_URL + "/logout", {}, { withCredentials: true })
      .subscribe({
        next: (response) => {
          console.log("LOGOUT: Successfully");
        },
        error: (error) => {
          console.error("Logout error:", error);
        },
        complete: () => {
          // Always clear state regardless of server response
          this.logged = false;
          this.user = undefined;
          this.authStateSubject.next(false);
          this.router.navigate(['/']);
        }
      });
  }

  /**
   * Check if user is currently logged in
   */
  public isLogged(): boolean {
    return this.logged;
  }

  /**
   * Check if user is currently logged in (compatibility method)
   */
  public isLoggedIn(): boolean {
    return this.logged;
  }

  /**
   * Check if current user is admin
   * Note: This might not work initially until we get role info
   */
  public isAdmin(): boolean {
    return this.user && this.user.role === "ROLE_ADMIN";
  }

  /**
   * Get current user
   */
  currentUser(): LoginUser | undefined {
    return this.user;
  }

  /**
   * Handle 401 errors from other services
   * Call this when any API returns 401 to mark user as logged out
   */
  public handleUnauthorized(): void {
    this.logged = false;
    this.user = undefined;
    this.authStateSubject.next(false);
    this.router.navigate(['/login']);
  }

  /**
   * Set the URL to redirect to after successful login
   */
  setRedirectUrl(url: string): void {
    localStorage.setItem('redirectUrl', url);
  }

  /**
   * Navigate to the stored redirect URL after successful login
   */
  redirectAfterLogin(): void {
    const redirectUrl = localStorage.getItem('redirectUrl') || '/books';
    localStorage.removeItem('redirectUrl');
    this.router.navigate([redirectUrl]);
  }

  /**
   * Get current user ID (might not be available without additional API call)
   */
  getCurrentUserId(): number | null {
    // We don't have user ID without calling another API
    // This would need to be fetched when needed
    return null;
  }

  /**
   * Set user info (can be called by other services that fetch user details)
   */
  public setUserInfo(userInfo: LoginUser): void {
    this.user = userInfo;
  }

  /**
   * Register a new user
   * Uses the REST API endpoint /api/auth/register
   */
  public register(username: string, email: string, encodedPassword: string): Observable<AuthResponse> {
    const registerData = {
      username: username,
      email: email,
      encodedPassword: encodedPassword
    };

    return this.http.post<AuthResponse>(
      BASE_URL + "/register",
      registerData
    ).pipe(
      tap((response: AuthResponse) => {
        console.log('Registration response:', response);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Registration failed:', error);
        throw error;
      })
    );
  }
}