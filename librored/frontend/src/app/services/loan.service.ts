import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { LoanDTO, LoanRequest } from "../dtos/loan.dto";

@Injectable({ providedIn: "root" })
export class LoanService {
  private readonly API_URL = "/api/loans";

  constructor(private http: HttpClient) {}

  // Get all loans (for admin) or user's loans
  getLoans(): Observable<LoanDTO[]> {
    return this.http.get<LoanDTO[]>(this.API_URL, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  // Get loans by lender
  getLoansByLender(lenderId: number): Observable<LoanDTO[]> {
    return this.http.get<LoanDTO[]>(`${this.API_URL}/lender/${lenderId}`, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  // Get loans by borrower
  getLoansByBorrower(borrowerId: number): Observable<LoanDTO[]> {
    return this.http.get<LoanDTO[]>(`${this.API_URL}/borrower/${borrowerId}`, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  // Get single loan by ID
  getLoan(id: number): Observable<LoanDTO> {
    return this.http.get<LoanDTO>(`${this.API_URL}/${id}`, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  // Create new loan
  createLoan(loan: LoanRequest): Observable<LoanDTO> {
    return this.http.post<LoanDTO>(this.API_URL, loan, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  // Update existing loan
  updateLoan(id: number, loan: Partial<LoanRequest>): Observable<LoanDTO> {
    return this.http.put<LoanDTO>(`${this.API_URL}/${id}`, loan, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  // Delete loan
  deleteLoan(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  // Get available books by lender ID (for loan creation/editing)
  getAvailableBooksByLender(lenderId: number): Observable<{id: number, title: string}[]> {
    return this.http.get<{id: number, title: string}[]>(`${this.API_URL}/books/${lenderId}`, { withCredentials: true })
      .pipe(catchError(this.handleError));
  }

  // Check if book is available for date range (for validation)
  isBookAvailableForDateRange(bookId: number, startDate: string, endDate?: string, excludeLoanId?: number): Observable<boolean> {
    let params: any = {
      bookId: bookId.toString(),
      startDate: startDate
    };
    
    if (endDate) params.endDate = endDate;
    if (excludeLoanId) params.excludeLoanId = excludeLoanId.toString();
    
    return this.http.get<boolean>(`${this.API_URL}/validate/book-availability`, { 
      params,
      withCredentials: true 
    }).pipe(catchError(this.handleError));
  }

  // Check if borrower is available for date range (for validation)
  isBorrowerAvailableForDateRange(borrowerId: number, lenderId: number, startDate: string, endDate?: string, excludeLoanId?: number): Observable<boolean> {
    let params: any = {
      borrowerId: borrowerId.toString(),
      lenderId: lenderId.toString(),
      startDate: startDate
    };
    
    if (endDate) params.endDate = endDate;
    if (excludeLoanId) params.excludeLoanId = excludeLoanId.toString();
    
    return this.http.get<boolean>(`${this.API_URL}/validate/borrower-availability`, { 
      params,
      withCredentials: true 
    }).pipe(catchError(this.handleError));
  }

  private handleError(error: any): Observable<never> {
    console.error('LoanService error:', error);
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