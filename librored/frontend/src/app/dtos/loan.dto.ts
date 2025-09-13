export interface LoanDTO {
  id?: number;
  bookId: number;
  bookTitle?: string;
  lenderId: number;
  lenderUsername?: string;
  borrowerId: number;
  borrowerUsername?: string;
  startDate: string; // Format: YYYY-MM-DD
  endDate?: string; // Format: YYYY-MM-DD, optional
  status: LoanStatus;
}

export enum LoanStatus {
  Active = 'Active',
  Completed = 'Completed'
}

export interface LoanRequest {
  bookId: number;
  lenderId: number;
  borrowerId: number;
  startDate: string;
  endDate?: string;
  status: LoanStatus;
}