import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { LoanService } from "../../services/loan.service";
import { BookService } from "../../services/book.service";
import { AuthService } from "../../services/auth.service";
import { UserService } from "../../services/user.service";
import { LoanDTO, LoanRequest, LoanStatus } from "../../dtos/loan.dto";
import { BookDTO } from "../../dtos/book.dto";
import { UserDTO } from "../../dtos/user.dto";

@Component({
  selector: "loan-form",
  templateUrl: "./loan-form.component.html",
})
export class LoanFormComponent implements OnInit {
  loan: LoanRequest = {
    bookId: 0,
    lenderId: 0,
    borrowerId: 0,
    startDate: '',
    endDate: '',
    status: LoanStatus.Active
  };
  
  isEditMode = false;
  loading = false;
  submitting = false;
  errorMessage = '';
  successMessage = '';
  
  availableBooks: {id: number, title: string}[] = [];
  availableUsers: UserDTO[] = [];
  isAdmin = false;
  currentUserId: number | null = null;
  
  loanStatuses = Object.values(LoanStatus);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private loanService: LoanService,
    private bookService: BookService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    // NO AUTH CHECK - Let backend handle it
    this.initializeComponent();
  }

  private initializeComponent(): void {
    // Skip user/admin checks - let backend handle permissions

    // Check if editing existing loan
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.isEditMode = true;
      this.loadLoan(id);
    } else {
      this.initializeForCreate();
    }
  }

  initializeForCreate(): void {
    this.loadUsers();
    // Load all books - let backend handle permissions
    
    // Set default start date to today
    const today = new Date();
    this.loan.startDate = today.toISOString().split('T')[0];
  }

  loadLoan(id: number): void {
    this.loading = true;
    this.loanService.getLoan(id).subscribe({
      next: (loan) => {
        this.loan = {
          bookId: loan.bookId,
          lenderId: loan.lenderId,
          borrowerId: loan.borrowerId,
          startDate: loan.startDate,
          endDate: loan.endDate || '',
          status: loan.status
        };
        this.loading = false;
        this.loadUsers();
        this.loadBooksByLender(loan.lenderId);
      },
      error: (error) => {
        console.error('Error loading loan:', error);
        this.errorMessage = 'Failed to load loan';
        this.loading = false;
      }
    });
  }

  loadUsers(): void {
    if (this.userService) {
      this.userService.getUsers().subscribe({
        next: (users) => {
          this.availableUsers = users;
        },
        error: (error) => {
          console.error('Error loading users:', error);
        }
      });
    }
  }

  loadBooksByLender(lenderId: number): void {
    if (!lenderId) {
      this.availableBooks = [];
      return;
    }

    this.loanService.getAvailableBooksByLender(lenderId).subscribe({
      next: (books) => {
        this.availableBooks = books;
      },
      error: (error) => {
        console.error('Error loading books:', error);
        this.availableBooks = [];
      }
    });
  }

  onLenderChange(): void {
    // Reset book selection when lender changes
    this.loan.bookId = 0;
    this.loadBooksByLender(this.loan.lenderId);
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';
    
    if (!this.validateForm()) {
      return;
    }

    this.submitting = true;

    const operation = this.isEditMode 
      ? this.loanService.updateLoan(Number(this.route.snapshot.paramMap.get('id')), this.loan)
      : this.loanService.createLoan(this.loan);

    operation.subscribe({
      next: (savedLoan) => {
        this.successMessage = this.isEditMode ? 'Loan updated successfully' : 'Loan created successfully';
        this.submitting = false;
        
        // Navigate back to loan list after short delay
        setTimeout(() => {
          this.router.navigate(['/loans']);
        }, 1500);
      },
      error: (error) => {
        console.error('Error saving loan:', error);
        this.errorMessage = error.message || 'Failed to save loan';
        this.submitting = false;
      }
    });
  }

  validateForm(): boolean {
    if (!this.loan.bookId) {
      this.errorMessage = 'Please select a book';
      return false;
    }

    if (!this.loan.lenderId) {
      this.errorMessage = 'Please select a lender';
      return false;
    }

    if (!this.loan.borrowerId) {
      this.errorMessage = 'Please select a borrower';
      return false;
    }

    if (this.loan.lenderId === this.loan.borrowerId) {
      this.errorMessage = 'Lender and borrower cannot be the same person';
      return false;
    }

    if (!this.loan.startDate) {
      this.errorMessage = 'Start date is required';
      return false;
    }

    // Validate start date is not in the past
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const startDate = new Date(this.loan.startDate);
    
    if (startDate < today && !this.isEditMode) {
      this.errorMessage = 'Start date must be today or in the future';
      return false;
    }

    // Validate end date if provided
    if (this.loan.endDate) {
      const endDate = new Date(this.loan.endDate);
      if (endDate <= startDate) {
        this.errorMessage = 'End date must be after start date';
        return false;
      }
    }

    return true;
  }

  cancel(): void {
    this.router.navigate(['/loans']);
  }

  getUserDisplayName(user: UserDTO): string {
    return user.username || user.email || `User ${user.id}`;
  }
}