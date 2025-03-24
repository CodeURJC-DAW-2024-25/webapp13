package es.codeurjc13.librored.service;

import es.codeurjc13.librored.dto.LoanCreateDTO;
import es.codeurjc13.librored.dto.LoanDTO;
import es.codeurjc13.librored.dto.LoanUpdateDTO;
import es.codeurjc13.librored.dto.UserDTO;
import es.codeurjc13.librored.mapper.LoanMapper;
import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.LoanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserService userService;
    private final BookService bookService;
    private final LoanMapper loanMapper;

    public LoanService(
            LoanRepository loanRepository,
            UserService userService,
            BookService bookService,
            LoanMapper loanMapper
    ) {
        this.loanRepository = loanRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.loanMapper = loanMapper;
    }

    public Page<LoanDTO> getAllLoansPaged(Pageable pageable) {
        List<Loan> loans = loanRepository.findAll(pageable).getContent();
        List<LoanDTO> dtos = loans.stream().map(loanMapper::toDTO).toList();
        return new PageImpl<>(dtos, pageable, loanRepository.count());
    }

    public List<LoanDTO> findAll(Authentication auth) {
        User currentUser = userService.getByEmail(auth.getName());

        if (currentUser.getRole().equals(User.Role.ROLE_ADMIN)) {
            return loanRepository.findAll().stream()
                    .map(loanMapper::toDTO)
                    .collect(Collectors.toList());
        } else {
            return loanRepository.findByBorrowerOrLender(currentUser).stream()
                    .map(loanMapper::toDTO)
                    .collect(Collectors.toList());
        }
    }

    public LoanDTO findById(Long id, Authentication auth) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        validateAccess(loan, auth);
        return loanMapper.toDTO(loan);
    }

    public Optional<Loan> getLoanByIdSecure(Long id, Authentication auth) {
        Optional<Loan> loanOpt = loanRepository.findById(id);
        if (loanOpt.isEmpty()) return Optional.empty();

        try {
            validateAccess(loanOpt.get(), auth);
            return loanOpt;
        } catch (AccessDeniedException e) {
            return Optional.empty();
        }
    }

    public boolean canEditLoan(Long id, Authentication auth) {
        return loanRepository.findById(id)
                .map(loan -> {
                    try {
                        validateAccess(loan, auth);
                        return true;
                    } catch (AccessDeniedException e) {
                        return false;
                    }
                }).orElse(false);
    }

    public LoanDTO createLoan(LoanCreateDTO dto, Authentication auth) {
        User currentUser = userService.getByEmail(auth.getName());

        User lender = userService.getById(dto.lenderId());
        User borrower = userService.getById(dto.borrowerId());

        if (!currentUser.equals(borrower) && !currentUser.getRole().equals(User.Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You are not allowed to create this loan");
        }

        Book book = bookService.findById(dto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        Loan loan = loanMapper.toDomain(dto);
        loan.setLender(lender);
        loan.setBorrower(borrower);
        loan.setBook(book);

        return loanMapper.toDTO(loanRepository.save(loan));
    }

    public LoanDTO updateLoan(Long id, LoanUpdateDTO dto, Authentication auth) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        validateAccess(loan, auth);

        loan.setStartDate(dto.startDate());
        loan.setEndDate(dto.endDate());
        loan.setStatus(Loan.Status.valueOf(dto.status()));

        return loanMapper.toDTO(loanRepository.save(loan));
    }

    public void deleteLoan(Long id, Authentication auth) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        validateAccess(loan, auth);
        loanRepository.delete(loan);
    }

    public List<UserDTO> getValidBorrowers(Long bookId) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        User owner = book.getOwner();

        return userService.findAll().stream()
                .filter(user -> !user.equals(owner))
                .map(userService::toDTO)
                .collect(Collectors.toList());
    }

    public List<Book> getAvailableBooksByLender(Long lenderId) {
        User lender = userService.getById(lenderId);
        return lender.getBooks().stream()
                .filter(book -> book.isAvailable() && !book.isCurrentlyOnLoan())
                .collect(Collectors.toList());
    }

    private void validateAccess(Loan loan, Authentication auth) {
        User currentUser = userService.getByEmail(auth.getName());

        boolean isAdmin = currentUser.getRole().equals(User.Role.ROLE_ADMIN);
        boolean isOwner = currentUser.equals(loan.getLender()) || currentUser.equals(loan.getBorrower());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Access denied to loan with id " + loan.getId());
        }
    }
}
