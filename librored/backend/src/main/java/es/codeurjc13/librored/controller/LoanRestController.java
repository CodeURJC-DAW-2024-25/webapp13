package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.dto.LoanCreateDTO;
import es.codeurjc13.librored.dto.LoanDTO;
import es.codeurjc13.librored.dto.LoanUpdateDTO;
import es.codeurjc13.librored.mapper.LoanMapper;
import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.LoanService;
import es.codeurjc13.librored.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanRestController {

    private final UserService userService;
    private final LoanService loanService;
    private final BookService bookService;
    private final LoanMapper loanMapper;

    public LoanRestController(LoanService loanService, BookService bookService, UserService userService, LoanMapper loanMapper) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.userService = userService;
        this.loanMapper = loanMapper;
    }

    @GetMapping("/valid-borrowers")
    public ResponseEntity<List<User>> getValidBorrowers(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User lender = userService.getUserByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        List<User> borrowers = userService.getValidBorrowers(lender);

        return ResponseEntity.ok(borrowers);
    }

    // GET ALL with pagination
    @GetMapping
    public ResponseEntity<Page<LoanDTO>> getAllLoans(Pageable pageable) {
        List<LoanDTO> dtos = loanService.getAllLoans().stream()
                .map(loanMapper::toDTO)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        Page<LoanDTO> page = new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());

        return ResponseEntity.ok(page);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id)
                .map(loanMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE
    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@RequestBody LoanCreateDTO dto) {
        Book book = bookService.getBookById(dto.bookId()).orElseThrow();
        User lender = userService.getUserById(dto.lenderId()).orElseThrow();
        User borrower = userService.getUserById(dto.borrowerId()).orElseThrow();

        loanService.createLoan(book, lender, borrower, dto.startDate(), dto.endDate(), Loan.Status.Active);

        // get last loan created by this user
        Loan created = loanService.getAllLoans()
                .stream()
                .filter(l -> l.getLender().getId().equals(dto.lenderId()) && l.getBorrower().getId().equals(dto.borrowerId()))
                .reduce((first, second) -> second)
                .orElseThrow();

        URI location = URI.create("/api/loans/" + created.getId());
        return ResponseEntity.created(location).body(loanMapper.toDTO(created));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLoan(@PathVariable Long id, @RequestBody LoanUpdateDTO dto) {
        try {
            Loan loan = loanService.getLoanById(id).orElseThrow();

            Loan updatedLoan = new Loan();
            updatedLoan.setId(id);
            updatedLoan.setLender(loan.getLender());
            updatedLoan.setBook(loan.getBook());
            updatedLoan.setBorrower(loan.getBorrower());
            updatedLoan.setStartDate(dto.startDate());
            updatedLoan.setEndDate(dto.endDate());

            if (dto.status() != null) {
                updatedLoan.setStatus(Loan.Status.valueOf(dto.status()));
            }

            loanService.updateLoan(id, updatedLoan);
            return ResponseEntity.ok(loanMapper.toDTO(loanService.getLoanById(id).orElseThrow()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        if (loanService.getLoanById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}
