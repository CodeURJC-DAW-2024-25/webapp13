import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { UserAccountService } from '../../services/user-account.service';
import { LoanService } from '../../services/loan.service';
import { BookDTO } from '../../dtos/book.dto';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

interface UserInfo {
  id: number;
  username: string;
  email: string;
  role: string;
}

@Component({
  selector: 'app-user-books',
  templateUrl: './user-books.component.html',
  styleUrls: ['./user-books.component.css']
})
export class UserBooksComponent implements OnInit {
  books: BookDTO[] = [];
  currentUser: UserInfo | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 5; // Show 5 books per page as requested
  totalItems = 0;
  totalPages = 0;

  // Loading states
  loading = false;
  loadingUser = false;

  // Modals
  showCreateModal = false;
  showEditModal = false;
  showDeleteModal = false;

  // Form data
  bookForm: BookDTO = this.getEmptyBookForm();
  selectedBookId: number | null = null;

  // Genres from backend enum
  genres = [
    'Fiction',
    'Non_Fiction',
    'Mystery_Thriller',
    'SciFi_Fantasy',
    'Romance',
    'Historical_Fiction',
    'Horror'
  ];

  // File upload
  selectedFile: File | null = null;

  // Error handling
  errorMessage = '';
  successMessage = '';

  // Math reference for template
  Math = Math;

  constructor(
    private bookService: BookService,
    private userAccountService: UserAccountService,
    private loanService: LoanService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  loadCurrentUser(): void {
    this.loadingUser = true;
    this.userAccountService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.loadingUser = false;
        this.loadUserBooks();
      },
      error: (error) => {
        console.error('Error loading current user:', error);
        this.errorMessage = 'Failed to load user information';
        this.loadingUser = false;
      }
    });
  }

  loadUserBooks(): void {
    if (!this.currentUser?.id) return;

    this.loading = true;
    this.bookService.getBooksByOwner(this.currentUser.id).subscribe({
      next: (books) => {
        // Load loan information for each book
        this.loadLoanInformationForBooks(books);
      },
      error: (error) => {
        console.error('Error loading user books:', error);
        this.errorMessage = 'Failed to load books';
        this.loading = false;
      }
    });
  }

  loadLoanInformationForBooks(books: BookDTO[]): void {
    if (books.length === 0) {
      this.books = books;
      this.totalItems = books.length;
      this.totalPages = Math.ceil(this.totalItems / this.pageSize);
      this.loading = false;
      return;
    }

    // Create observables for each book's loan information
    const loanRequests = books.map(book => {
      if (book.id) {
        return this.loanService.getActiveLoansForBook(book.id).pipe(
          map(activeLoans => {
            book.isCurrentlyOnLoan = activeLoans.length > 0;
            if (activeLoans.length > 0) {
              const activeLoan = activeLoans[0];
              book.currentLoanInfo = {
                borrower: activeLoan.borrower.username,
                startDate: activeLoan.startDate,
                endDate: activeLoan.endDate || undefined
              };
            }
            return book;
          }),
          catchError(() => {
            // On error, just mark as not on loan
            book.isCurrentlyOnLoan = false;
            return of(book);
          })
        );
      } else {
        book.isCurrentlyOnLoan = false;
        return of(book);
      }
    });

    // Wait for all loan requests to complete
    forkJoin(loanRequests).subscribe({
      next: (booksWithLoanInfo) => {
        this.books = booksWithLoanInfo;
        this.totalItems = booksWithLoanInfo.length;
        this.totalPages = Math.ceil(this.totalItems / this.pageSize);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading loan information:', error);
        // Even on error, show books without loan info
        this.books = books;
        this.totalItems = books.length;
        this.totalPages = Math.ceil(this.totalItems / this.pageSize);
        this.loading = false;
      }
    });
  }

  // Pagination helpers
  get paginatedBooks(): BookDTO[] {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    return this.books.slice(start, end);
  }

  get isFirst(): boolean {
    return this.currentPage === 0;
  }

  get isLast(): boolean {
    return this.currentPage >= this.totalPages - 1;
  }

  getPageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  previousPage(): void {
    if (!this.isFirst) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (!this.isLast) {
      this.currentPage++;
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
  }

  // Modal management
  openCreateModal(): void {
    this.bookForm = this.getEmptyBookForm();
    this.selectedFile = null;
    this.clearMessages();
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.bookForm = this.getEmptyBookForm();
    this.selectedFile = null;
    // Clear the file input element
    this.clearFileInput('createCoverImage');
  }

  openEditModal(book: BookDTO): void {
    this.bookForm = { ...book };
    this.selectedBookId = book.id;
    this.selectedFile = null;
    this.clearMessages();
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.bookForm = this.getEmptyBookForm();
    this.selectedFile = null;
    this.selectedBookId = null;
    // Clear the file input element
    this.clearFileInput('editCoverImage');
  }

  openDeleteModal(book: BookDTO): void {
    this.bookForm = { ...book };
    this.selectedBookId = book.id;
    this.clearMessages();
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.bookForm = this.getEmptyBookForm();
    this.selectedBookId = null;
  }

  // CRUD operations
  createBook(): void {
    if (!this.currentUser?.id) {
      this.errorMessage = 'User not authenticated';
      return;
    }

    // Set current user as owner
    this.bookForm.owner = {
      id: this.currentUser.id,
      username: this.currentUser.username
    };

    // Check for file selection BEFORE creating book
    const fileInput = document.getElementById('createCoverImage') as HTMLInputElement;
    const selectedFile = fileInput?.files?.[0] || this.selectedFile || null;

    // Set hasCoverImage flag to true if a file is selected - the image will be uploaded after book creation
    this.bookForm.hasCoverImage = selectedFile !== null;


    this.loading = true;
    this.bookService.createBook(this.bookForm).subscribe({
      next: (createdBook) => {

        this.successMessage = 'Book created successfully!';
        this.loading = false;

        // Get file directly from DOM before modal is closed
        const fileInput = document.getElementById('createCoverImage') as HTMLInputElement;
        const fileToUpload = fileInput?.files?.[0] || null;

        this.closeCreateModal();

        // Upload cover image if selected
        if (fileToUpload) {
          this.uploadCoverImageDirect(createdBook.id, fileToUpload);
        } else {
          this.loadUserBooks();
        }
      },
      error: (error) => {
        console.error('Error creating book:', error);
        this.errorMessage = 'Failed to create book';
        this.loading = false;
      }
    });
  }

  updateBook(): void {
    if (!this.selectedBookId) return;

    // Save the file reference before any operations that might clear it
    const fileToUpload = this.selectedFile;

    this.loading = true;
    this.bookService.updateBook(this.selectedBookId, this.bookForm).subscribe({
      next: (updatedBook) => {
        this.successMessage = 'Book updated successfully!';
        this.loading = false;
        this.closeEditModal();

        // Upload cover image if selected
        if (fileToUpload) {
          this.uploadCoverImageDirect(updatedBook.id, fileToUpload);
        } else {
          this.loadUserBooks();
        }
      },
      error: (error) => {
        console.error('Error updating book:', error);
        this.errorMessage = 'Failed to update book';
        this.loading = false;
      }
    });
  }

  deleteBook(): void {
    if (!this.selectedBookId) return;

    this.loading = true;
    this.bookService.deleteBook(this.selectedBookId).subscribe({
      next: () => {
        this.successMessage = 'Book deleted successfully!';
        this.loading = false;
        this.closeDeleteModal();
        this.loadUserBooks();
      },
      error: (error) => {
        console.error('Error deleting book:', error);
        this.errorMessage = 'Failed to delete book';
        this.loading = false;
      }
    });
  }

  // File handling

  uploadCoverImage(bookId: number): void {
    if (!this.selectedFile) {
      this.loadUserBooks();
      return;
    }


    this.bookService.uploadCoverImage(bookId, this.selectedFile).subscribe({
      next: (response) => {
        this.successMessage = 'Cover image uploaded successfully!';
        this.loadUserBooks();
      },
      error: (error) => {
        this.errorMessage = 'Failed to upload cover image: ' + (error.error?.message || error.message);
        this.loadUserBooks();
      }
    });
  }

  uploadCoverImageDirect(bookId: number, file: File): void {
    this.bookService.uploadCoverImage(bookId, file).subscribe({
      next: (response) => {
        this.successMessage = 'Book and cover image created successfully!';
        this.loadUserBooks();
      },
      error: (error) => {
        console.error('Error uploading cover image:', error);
        this.errorMessage = 'Book created but failed to upload cover image: ' + (error.error?.message || error.message);

        // Update the book to set hasCoverImage to false since upload failed
        const bookToUpdate = { ...this.bookForm, id: bookId, hasCoverImage: false };
        this.bookService.updateBook(bookId, bookToUpdate).subscribe({
          next: () => {
            this.loadUserBooks();
          },
          error: (updateError) => {
            console.error('Failed to update book after image upload failure:', updateError);
            this.loadUserBooks();
          }
        });
      }
    });
  }

  getCoverImageUrl(book: BookDTO): string {
    if (book.hasCoverImage && book.id) {
      return this.bookService.getCoverImageUrl(book.id);
    }
    return '/images/default_cover.jpg';
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    } else {
      this.selectedFile = null;
    }
  }

  // Helper methods
  getEmptyBookForm(): BookDTO {
    return {
      id: null,
      title: '',
      author: '',
      genre: '',
      description: '',
      hasCoverImage: false,
      owner: undefined
    };
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  clearFileInput(elementId: string): void {
    const fileInput = document.getElementById(elementId) as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }
}