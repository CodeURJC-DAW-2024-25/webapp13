import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = '/api/auth';
  private loggedIn = false;
  private currentUser: any = null;

  constructor(private http: HttpClient) {}

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
   * Set login status (called after successful authentication)
   */
  setLoggedIn(user: any = null): void {
    this.loggedIn = true;
    this.currentUser = user || { email: 'user@example.com', name: 'User' };
  }

  /**
   * Clear login status (called after logout)
   */
  setLoggedOut(): void {
    this.loggedIn = false;
    this.currentUser = null;
  }
}