package es.codeurjc13.librored.service;

import es.codeurjc13.librored.dto.LoanDTO;
import es.codeurjc13.librored.mapper.LoanMapper;
import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.BookRepository;
import es.codeurjc13.librored.repository.LoanRepository;
import es.codeurjc13.librored.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LoanMapper loanMapper;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private PlatformTransactionManager transactionManager;

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

    public void deleteLoan(Long id) {
        // Use manual transaction management for reliable deletion
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        
        try {
            Optional<Loan> loan = loanRepository.findById(id);
            if (loan.isPresent()) {
                // Use native SQL for direct deletion to bypass JPA/Hibernate issues
                int rowsAffected = entityManager.createNativeQuery("DELETE FROM loan WHERE id = ?1")
                    .setParameter(1, id)
                    .executeUpdate();
                
                entityManager.flush();
                
                if (rowsAffected == 0) {
                    transactionManager.rollback(transaction);
                    throw new RuntimeException("Database constraint preventing deletion");
                }
                
                // Commit the transaction immediately
                transactionManager.commit(transaction);
                
            } else {
                transactionManager.rollback(transaction);
                throw new RuntimeException("Loan not found");
            }
            
        } catch (Exception e) {
            if (!transaction.isCompleted()) {
                transactionManager.rollback(transaction);
            }
            throw new RuntimeException("Failed to delete loan: " + e.getMessage());
        }
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

    // ==================== DTO-BASED METHODS FOR REST API ====================

    public List<LoanDTO> getAllLoansDTO() {
        List<Loan> loans = loanRepository.findAll();
        return loanMapper.toDTOs(loans);
    }

    public Optional<LoanDTO> getLoanByIdDTO(Long id) {
        Optional<Loan> loan = loanRepository.findById(id);
        return loan.map(loanMapper::toDTO);
    }

    public LoanDTO createLoanDTO(LoanDTO loanDTO) {
        Loan loan = loanMapper.toDomain(loanDTO);
        
        // Set book, lender, and borrower from DTO references
        if (loanDTO.book() != null) {
            Optional<Book> book = bookRepository.findById(loanDTO.book().id());
            if (book.isPresent()) {
                loan.setBook(book.get());
            } else {
                throw new IllegalArgumentException("Book not found with id: " + loanDTO.book().id());
            }
        }
        
        if (loanDTO.lender() != null) {
            Optional<User> lender = userRepository.findById(loanDTO.lender().id());
            if (lender.isPresent()) {
                loan.setLender(lender.get());
            } else {
                throw new IllegalArgumentException("Lender not found with id: " + loanDTO.lender().id());
            }
        }
        
        if (loanDTO.borrower() != null) {
            Optional<User> borrower = userRepository.findById(loanDTO.borrower().id());
            if (borrower.isPresent()) {
                loan.setBorrower(borrower.get());
            } else {
                throw new IllegalArgumentException("Borrower not found with id: " + loanDTO.borrower().id());
            }
        }
        
        // Validate business rules
        validateLoanBusinessRules(loan, null);
        
        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDTO(savedLoan);
    }

    public Optional<LoanDTO> updateLoanDTO(Long id, LoanDTO loanDTO) {
        Optional<Loan> existingLoanOpt = loanRepository.findById(id);
        if (existingLoanOpt.isPresent()) {
            Loan loan = existingLoanOpt.get();

            // ✅ Prevent lender change (Fixed Lender Rule)
            if (loanDTO.lender() != null && !loan.getLender().getId().equals(loanDTO.lender().id())) {
                throw new IllegalArgumentException(
                        "Lender cannot be changed. The loan must remain under " + loan.getLender().getUsername() + ".");
            }

            // Update book if provided
            if (loanDTO.book() != null) {
                Optional<Book> book = bookRepository.findById(loanDTO.book().id());
                if (book.isPresent()) {
                    List<Book> availableBooks = bookRepository.findAvailableBooksByOwnerId(loan.getLender().getId());
                    if (!availableBooks.contains(book.get())) {
                        throw new IllegalArgumentException(
                                "The selected book is either not owned by " + loan.getLender().getUsername() +
                                        " or is currently loaned out. Please choose an available book.");
                    }
                    loan.setBook(book.get());
                } else {
                    throw new IllegalArgumentException("Book not found with id: " + loanDTO.book().id());
                }
            }

            // Update borrower if provided
            if (loanDTO.borrower() != null) {
                Optional<User> borrower = userRepository.findById(loanDTO.borrower().id());
                if (borrower.isPresent()) {
                    loan.setBorrower(borrower.get());
                } else {
                    throw new IllegalArgumentException("Borrower not found with id: " + loanDTO.borrower().id());
                }
            }

            // Update dates
            if (loanDTO.startDate() != null) {
                loan.setStartDate(loanDTO.startDate());
            }
            if (loanDTO.endDate() != null) {
                if (loanDTO.endDate().isBefore(loan.getStartDate())) {
                    throw new IllegalArgumentException(
                            "End date cannot be before the start date. Please select a date after " + loan.getStartDate() + ".");
                }
                loan.setEndDate(loanDTO.endDate());
            }

            // Update status with validation
            if (loanDTO.status() != null) {
                if (loan.getStatus() == Loan.Status.Completed && loanDTO.status() == Loan.Status.Active) {
                    throw new IllegalArgumentException(
                            "A completed loan cannot be reactivated. Consider creating a new loan instead.");
                }
                loan.setStatus(loanDTO.status());
            }

            Loan savedLoan = loanRepository.save(loan);
            return Optional.of(loanMapper.toDTO(savedLoan));
        }
        return Optional.empty();
    }

    public boolean deleteLoanDTO(Long id) {
        // Use the same reliable deletion method as the entity version
        try {
            deleteLoan(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<LoanDTO> getLoansByLenderIdDTO(Long lenderId) {
        Optional<User> lender = userRepository.findById(lenderId);
        if (lender.isPresent()) {
            List<Loan> loans = loanRepository.findByLender(lender.get());
            return loanMapper.toDTOs(loans);
        }
        return List.of();
    }

    public List<LoanDTO> getLoansByBorrowerIdDTO(Long borrowerId) {
        Optional<User> borrower = userRepository.findById(borrowerId);
        if (borrower.isPresent()) {
            List<Loan> loans = loanRepository.findByBorrower(borrower.get());
            return loanMapper.toDTOs(loans);
        }
        return List.of();
    }

    public List<LoanDTO> getLoansByBookIdDTO(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            List<Loan> loans = loanRepository.findByBook(book.get());
            return loanMapper.toDTOs(loans);
        }
        return List.of();
    }

    /**
     * Validate business rules for loan creation/update
     */
    private void validateLoanBusinessRules(Loan loan, Long excludeLoanId) {
        // Check book availability for the date range
        if (!isBookAvailableForDateRange(loan.getBook().getId(), loan.getStartDate(), loan.getEndDate(), excludeLoanId)) {
            throw new IllegalArgumentException("Book is not available for the selected date range.");
        }

        // Check borrower availability for the date range
        if (!isBorrowerAvailableForDateRange(loan.getBorrower().getId(), loan.getLender().getId(), 
                loan.getStartDate(), loan.getEndDate(), excludeLoanId)) {
            throw new IllegalArgumentException("Borrower already has an active loan from this lender during the selected date range.");
        }

        // Ensure lender owns the book
        if (!loan.getBook().getOwner().equals(loan.getLender())) {
            throw new IllegalArgumentException("Lender must be the owner of the book.");
        }

        // Ensure lender and borrower are different
        if (loan.getLender().equals(loan.getBorrower())) {
            throw new IllegalArgumentException("Lender and borrower cannot be the same person.");
        }
    }

}

