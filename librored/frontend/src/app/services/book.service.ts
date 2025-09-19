import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BookDTO } from '../dtos/book.dto';
import { PaginatedResponse } from '../interfaces/paginated-response.interface';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private readonly API_URL = '/api/books'; // For public books
  private readonly ADMIN_API_URL = '/api/v1/books'; // For admin CRUD operations

  constructor(private http: HttpClient) {}

  /**
   * Get all books (legacy method for backwards compatibility)
   */
  getBooks(): Observable<BookDTO[]> {
    return this.getBooksPaginated(0, 8).pipe(
      map(response => response.content)
    );
  }

  /**
   * Get books with pagination
   */
  getBooksPaginated(page: number = 0, size: number = 8): Observable<PaginatedResponse<BookDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedResponse<BookDTO>>(this.API_URL, {
      params,
      withCredentials: true
    });
  }

  /**
   * Get book by ID
   */
  getBook(id: number): Observable<BookDTO> {
    return this.http.get<BookDTO>(`${this.ADMIN_API_URL}/${id}`);
  }

  /**
   * ADMIN METHODS - Full CRUD operations for admin users
   */

  /**
   * Get all books with pagination (admin)
   */
  getAllBooksPaginated(page: number = 0, size: number = 10): Observable<PaginatedResponse<BookDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedResponse<BookDTO>>(this.ADMIN_API_URL, { params });
  }

  /**
   * Create new book (admin)
   */
  createBook(book: BookDTO): Observable<BookDTO> {
    return this.http.post<BookDTO>(this.ADMIN_API_URL, book);
  }

  /**
   * Update existing book (admin)
   */
  updateBook(id: number, book: BookDTO): Observable<BookDTO> {
    return this.http.put<BookDTO>(`${this.ADMIN_API_URL}/${id}`, book);
  }

  /**
   * Delete book (admin)
   */
  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.ADMIN_API_URL}/${id}`);
  }

  /**
   * Upload book cover image (admin)
   */
  uploadCoverImage(id: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.ADMIN_API_URL}/${id}/cover`, formData);
  }

  /**
   * Get book cover image URL
   */
  getCoverImageUrl(id: number): string {
    return `${this.ADMIN_API_URL}/${id}/cover`;
  }

  /**
   * Get book image URL (legacy)
   */
  getImageUrl(id: number): string {
    return `${this.API_URL}/${id}/cover`;
  }
}