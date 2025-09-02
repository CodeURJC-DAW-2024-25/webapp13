package es.codeurjc13.librored.service;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.BookRepository;
import es.codeurjc13.librored.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public void updateLoan(Long id, Loan updatedLoan) {
        Optional<Loan> existingLoanOpt = loanRepository.findById(id);
        if (existingLoanOpt.isPresent()) {
            Loan loan = existingLoanOpt.get();

            // ✅ Prevent lender change (Fixed Lender Rule)
            if (!loan.getLender().equals(updatedLoan.getLender())) {
                throw new IllegalArgumentException(
                        "Lender cannot be changed. The loan must remain under " + loan.getLender().getUsername() + ".");
            }

            // ✅ Ensure the book belongs to the lender and is not currently loaned
            if (updatedLoan.getBook() != null) {
                List<Book> availableBooks = bookRepository.findAvailableBooksByOwnerId(loan.getLender().getId());
                if (!availableBooks.contains(updatedLoan.getBook())) {
                    throw new IllegalArgumentException(
                            "The selected book is either not owned by " + loan.getLender().getUsername() +
                                    " or is currently loaned out. Please choose an available book.");
                }
                loan.setBook(updatedLoan.getBook());
            }

            // ✅ Ensure borrower is valid
            if (updatedLoan.getBorrower() != null) {
                loan.setBorrower(updatedLoan.getBorrower());
            }

            // ✅ Validate start and end dates
            if (updatedLoan.getStartDate() != null) {
                loan.setStartDate(updatedLoan.getStartDate());
            }
            if (updatedLoan.getEndDate() != null) {
                if (updatedLoan.getEndDate().isBefore(loan.getStartDate())) {
                    throw new IllegalArgumentException(
                            "End date cannot be before the start date. Please select a date after " + loan.getStartDate() + ".");
                }
                loan.setEndDate(updatedLoan.getEndDate());
            }

            // ✅ Ensure status change is logical
            if (updatedLoan.getStatus() != null) {
                if (loan.getStatus() == Loan.Status.Completed && updatedLoan.getStatus() == Loan.Status.Active) {
                    throw new IllegalArgumentException(
                            "A completed loan cannot be reactivated. Consider creating a new loan instead.");
                }
                loan.setStatus(updatedLoan.getStatus());
            }

            loanRepository.save(loan);
        }
    }

    public void createLoan(Book book, User lender, User borrower, LocalDate startDate, LocalDate endDate, Loan.Status status) {
        Loan loan = new Loan(book, lender, borrower, startDate, endDate, status);
        loanRepository.save(loan);
    }

    @Transactional
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    public void saveLoan(Loan loan) {
        loanRepository.save(loan);
    }

    public List<Loan> getLoansByLender(User lender) {
        return loanRepository.findByLender(lender);
    }

    /**
     * Check if a book is available during a specific date range (excluding current loan being edited)
     * @param bookId The book to check
     * @param startDate Start date of the period
     * @param endDate End date of the period (can be null for open-ended loans)
     * @param excludeLoanId Loan ID to exclude from the check (when editing existing loan)
     * @return true if book is available, false if there's a conflict
     */
    public boolean isBookAvailableForDateRange(Long bookId, LocalDate startDate, LocalDate endDate, Long excludeLoanId) {
        List<Loan> overlappingLoans = loanRepository.findOverlappingLoans(bookId, startDate, endDate, excludeLoanId);
        return overlappingLoans.isEmpty();
    }

    /**
     * Check if a borrower already has an active loan from the same lender during the date range
     */
    public boolean isBorrowerAvailableForDateRange(Long borrowerId, Long lenderId, LocalDate startDate, LocalDate endDate, Long excludeLoanId) {
        List<Loan> overlappingBorrowerLoans = loanRepository.findOverlappingBorrowerLoans(borrowerId, lenderId, startDate, endDate, excludeLoanId);
        return overlappingBorrowerLoans.isEmpty();
    }

}

