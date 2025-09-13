import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { UserDTO } from "../dtos/user.dto";

@Injectable({ providedIn: "root" })
export class UserService {
  private readonly API_URL = "/api/users";

  constructor(private http: HttpClient) {}

  // Get all users (for admin or selection dropdowns)
  getUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(this.API_URL, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  // Get single user by ID
  getUser(id: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.API_URL}/${id}`, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  // Get current user profile
  getCurrentUser(): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.API_URL}/me`, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  private handleError(error: any): Observable<never> {
    console.error('UserService error:', error);
    let errorMessage = 'An error occurred';
    
    if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.message) {
      errorMessage = error.message;
    } else if (typeof error.error === 'string') {
      errorMessage = error.error;
    }
    
    return throwError(() => new Error(errorMessage));
  }
}