import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserDTO {
  id: number;
  username: string;
  email: string;
  role: 'ROLE_USER' | 'ROLE_ADMIN';
}

export interface UserBasicDTO {
  id: number;
  username: string;
  email: string;
}

export interface PaginatedUsersResponse {
  content: UserDTO[];
  currentPage: number;
  totalPages: number;
  totalItems: number;
  hasNext: boolean;
  hasPrevious: boolean;
  isFirst: boolean;
  isLast: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly API_URL = '/api/v1/users';

  constructor(private http: HttpClient) {}

  /**
   * Get all users with pagination
   */
  getAllUsers(page: number = 0, size: number = 10): Observable<PaginatedUsersResponse> {
    return this.http.get<PaginatedUsersResponse>(
      `${this.API_URL}?page=${page}&size=${size}`,
      { withCredentials: true }
    );
  }

  /**
   * Get user by ID
   */
  getUserById(id: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(
      `${this.API_URL}/${id}`,
      { withCredentials: true }
    );
  }

  /**
   * Create new user
   */
  createUser(user: Omit<UserDTO, 'id'>): Observable<UserDTO> {
    return this.http.post<UserDTO>(
      this.API_URL,
      user,
      { withCredentials: true }
    );
  }

  /**
   * Update existing user
   */
  updateUser(id: number, user: Omit<UserDTO, 'id'>): Observable<UserDTO> {
    return this.http.put<UserDTO>(
      `${this.API_URL}/${id}`,
      user,
      { withCredentials: true }
    );
  }

  /**
   * Delete user by ID
   */
  deleteUser(id: number): Observable<any> {
    return this.http.delete(
      `${this.API_URL}/${id}`,
      { withCredentials: true }
    );
  }

  /**
   * Get user by username
   */
  getUserByUsername(username: string): Observable<UserDTO> {
    return this.http.get<UserDTO>(
      `${this.API_URL}/username/${username}`,
      { withCredentials: true }
    );
  }

  /**
   * Get user by email
   */
  getUserByEmail(email: string): Observable<UserDTO> {
    return this.http.get<UserDTO>(
      `${this.API_URL}/email/${email}`,
      { withCredentials: true }
    );
  }

  /**
   * Download admin report (PDF)
   */
  downloadAdminReport(): Observable<Blob> {
    return this.http.get('/admin/download-report', {
      responseType: 'blob',
      withCredentials: true
    });
  }
}