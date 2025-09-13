import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookDTO } from '../dtos/book.dto';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private readonly API_URL = '/api/books';

  constructor(private http: HttpClient) {}

  /**
   * Get all books
   */
  getBooks(): Observable<BookDTO[]> {
    return this.http.get<BookDTO[]>(this.API_URL, { withCredentials: true });
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
    return this.http.post(`${this.API_URL}/${id}/image`, formData, { withCredentials: true });
  }

  /**
   * Get book image URL
   */
  getImageUrl(id: number): string {
    return `${this.API_URL}/${id}/image`;
  }
}