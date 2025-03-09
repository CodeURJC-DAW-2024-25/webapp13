package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.LoanService;
import es.codeurjc13.librored.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanRestController {

    private final LoanService loanService;
    private final BookService bookService;
    private final UserService userService;

    public LoanRestController(LoanService loanService, BookService bookService, UserService userService) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.userService = userService;
    }

    @PostMapping(value = "/{id}/edit", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Map<String, Object>> updateLoan(
            @PathVariable Long id,
            @RequestParam(value = "bookId", required = false) Long newBookId,
            @RequestParam(value = "borrowerId", required = false) Long newBorrowerId,
            @RequestParam(value = "startDate", required = false) String newStartDate,
            @RequestParam(value = "endDate", required = false) String newEndDate,
            @RequestParam(value = "status", required = false) String newStatus) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> updatedFields = new HashMap<>();

        Loan loan = loanService.getLoanById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        try {
            if (newBookId != null) {
                Book newBook = bookService.getBookById(newBookId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid book ID"));
                loan.setBook(newBook);
                updatedFields.put("book", newBook.getTitle());
            }

            if (newBorrowerId != null) {
                User newBorrower = userService.getUserById(newBorrowerId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid borrower ID"));
                loan.setBorrower(newBorrower);
                updatedFields.put("borrower", newBorrower.getUsername());
            }

            if (newStartDate != null) {
                loan.setStartDate(LocalDate.parse(newStartDate));
                updatedFields.put("startDate", newStartDate);
            }

            if (newEndDate != null) {
                LocalDate parsedEndDate = LocalDate.parse(newEndDate);
                if (parsedEndDate.isBefore(loan.getStartDate())) {
                    return ResponseEntity.ok(Map.of("success", false, "error", "End date must be after start date."));
                }
                loan.setEndDate(parsedEndDate);
                updatedFields.put("endDate", newEndDate);
            }

            if (newStatus != null) {
                loan.setStatus(Loan.Status.valueOf(newStatus));
                updatedFields.put("status", newStatus);
            }

            loanService.saveLoan(loan);
            response.put("success", true);
            response.put("message", "Loan updated successfully!");
            response.put("updatedFields", updatedFields);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
