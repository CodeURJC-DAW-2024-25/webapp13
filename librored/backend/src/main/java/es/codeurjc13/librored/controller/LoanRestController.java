package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.dto.LoanCreateDTO;
import es.codeurjc13.librored.dto.LoanDTO;
import es.codeurjc13.librored.dto.LoanUpdateDTO;
import es.codeurjc13.librored.dto.UserDTO;
import es.codeurjc13.librored.mapper.LoanMapper;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.LoanService;
import es.codeurjc13.librored.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loans", description = "Loan management API")
public class LoanRestController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;
    @Autowired
    private UserService userService;


    public LoanRestController(LoanService loanService, LoanMapper loanMapper) {
        this.loanService = loanService;
        this.loanMapper = loanMapper;
    }

    @Operation(summary = "Get valid borrowers", description = "Retrieve a list of users eligible to borrow books from the current user.")
    @ApiResponse(responseCode = "200", description = "Borrower list retrieved successfully")
    @ApiResponse(responseCode = "401", description = "User is not authenticated")
    @GetMapping("/valid-borrowers")
    public ResponseEntity<List<UserDTO>> getValidBorrowers(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User lender = userService.getByEmail(auth.getName());
        List<UserDTO> borrowers = userService.getValidBorrowers(lender)
                .stream()
                .map(userService::toDTO)
                .toList();

        return ResponseEntity.ok(borrowers);
    }


    @Operation(summary = "Get all loans", description = "Retrieve a paginated list of all loans.")
    @ApiResponse(responseCode = "200", description = "Loans retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<LoanDTO>> getAllLoans(Pageable pageable) {
        Page<LoanDTO> loans = loanService.getAllLoansPaged(pageable);
        return ResponseEntity.ok(loans);
    }

    @Operation(summary = "Get loan by ID", description = "Retrieve a specific loan by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan found", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable Long id, Authentication auth) {
        return loanService.getLoanByIdSecure(id, auth)
                .map(loanMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @Operation(summary = "Create loan", description = "Create a new loan between a lender and borrower for a book.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan created", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
            @ApiResponse(responseCode = "404", description = "Book or user not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@RequestBody LoanCreateDTO dto, Authentication auth) {
        LoanDTO createdLoan = loanService.createLoan(dto, auth);
        URI location = URI.create("/api/loans/" + createdLoan.id());
        return ResponseEntity.created(location).body(createdLoan);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    public ResponseEntity<Void> updateLoan(@PathVariable Long id, @RequestBody LoanUpdateDTO dto, Authentication auth) {
        if (!loanService.canEditLoan(id, auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        loanService.updateLoan(id, dto, auth);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loan deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id, Authentication auth) {
        if (!loanService.canEditLoan(id, auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        loanService.deleteLoan(id, auth);
        return ResponseEntity.noContent().build();
    }

    private boolean isOwnerOrAdmin(Loan loan, User user) {
        return loan.getLender().getId().equals(user.getId()) || user.getRole().name().equals("ROLE_ADMIN");
    }

}
