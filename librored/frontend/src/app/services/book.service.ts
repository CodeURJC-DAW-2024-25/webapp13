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
  private readonly API_URL = '/api/books';

  constructor(private http: HttpClient) {} // Updated for JWT

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
    return this.http.get<BookDTO>(`${this.API_URL}/${id}`, { withCredentials: true });
  }

  /**
   * Create new book
   */
  createBook(book: BookDTO): Observable<BookDTO> {
    return this.http.post<BookDTO>(this.API_URL, book, { withCredentials: true });
  }

  /**
   * Update existing book
   */
  updateBook(id: number, book: BookDTO): Observable<BookDTO> {
    return this.http.put<BookDTO>(`${this.API_URL}/${id}`, book, { withCredentials: true });
  }

  /**
   * Delete book
   */
  deleteBook(id: number): Observable<BookDTO> {
    return this.http.delete<BookDTO>(`${this.API_URL}/${id}`, { withCredentials: true });
  }

  /**
   * Upload book image
   */
  uploadImage(id: number, imageFile: File): Observable<any> {
    const formData = new FormData();
    formData.append('imageFile', imageFile);
    return this.http.post(`${this.API_URL}/${id}/cover`, formData, { withCredentials: true });
  }

  /**
   * Get book image URL
   */
  getImageUrl(id: number): string {
    return `${this.API_URL}/${id}/cover`;
  }
}