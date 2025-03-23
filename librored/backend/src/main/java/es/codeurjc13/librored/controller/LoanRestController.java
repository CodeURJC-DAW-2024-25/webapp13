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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Loans", description = "Loan management API")
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

    @Operation(summary = "Get valid borrowers", description = "Retrieve a list of users eligible to borrow books from the current user.")
    @ApiResponse(responseCode = "200", description = "Borrower list retrieved successfully")
    @ApiResponse(responseCode = "401", description = "User is not authenticated")
    @GetMapping("/valid-borrowers")
    public ResponseEntity<List<User>> getValidBorrowers(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User lender = userService.getUserByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        List<User> borrowers = userService.getValidBorrowers(lender);

        return ResponseEntity.ok(borrowers);
    }

    @GetMapping
    @Operation(summary = "Get all loans", description = "Retrieve a paginated list of all loans.")
    @ApiResponse(responseCode = "200", description = "Loans retrieved successfully")
    public ResponseEntity<Page<LoanDTO>> getAllLoans(Pageable pageable) {
        List<LoanDTO> dtos = loanService.getAllLoans().stream()
                .map(loanMapper::toDTO)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        Page<LoanDTO> page = new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());

        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get loan by ID", description = "Retrieve a specific loan by its ID.")
    @ApiResponse(responseCode = "200", description = "Loan found", content = @Content(schema = @Schema(implementation = LoanDTO.class)))
    @ApiResponse(responseCode = "404", description = "Loan not found")
    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id)
                .map(loanMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create loan", description = "Create a new loan between a lender and borrower for a book.")
    @ApiResponse(responseCode = "201", description = "Loan created", content = @Content(schema = @Schema(implementation = LoanDTO.class)))
    @ApiResponse(responseCode = "404", description = "Book or user not found")
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

    @Operation(summary = "Update loan", description = "Update an existing loan by ID.")
    @ApiResponse(responseCode = "200", description = "Loan updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid loan data provided")
    @ApiResponse(responseCode = "404", description = "Loan not found")
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


    @Operation(summary = "Delete loan", description = "Delete a loan by its ID.")
    @ApiResponse(responseCode = "204", description = "Loan deleted successfully")
    @ApiResponse(responseCode = "404", description = "Loan not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        if (loanService.getLoanById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}
