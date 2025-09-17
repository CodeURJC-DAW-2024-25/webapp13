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
  email: string;
  roles?: string[];
  id?: number;
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
    console.log('🚀 AuthService constructor - NO AUTO AUTH CHECK');
    // NO AUTO AUTH CHECK - Call manually when needed
  }

  /**
   * Login with username and password (professor's pattern)
   */
  public logIn(user: string, pass: string) {
    return this.http.post(
      BASE_URL + "/login",
      { username: user, password: pass },
      { withCredentials: true }
    );
  }

  /**
   * Logout current user (professor's pattern)
   */
  public logOut() {
    return this.http
      .post(BASE_URL + "/logout", {}, { withCredentials: true })
      .subscribe((_) => {
        console.log("LOGOUT: Successfully");
        this.logged = false;
        this.user = undefined;
        this.authStateSubject.next(false);
        this.router.navigate(['/']);
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
   * Check if current user is admin (professor's pattern)
   */
  public isAdmin(): boolean {
    return this.user && this.user.roles && this.user.roles.indexOf("ADMIN") !== -1;
  }

  /**
   * Get current user
   */
  currentUser(): LoginUser | undefined {
    return this.user;
  }

}