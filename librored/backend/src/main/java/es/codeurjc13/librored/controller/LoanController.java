package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.LoanService;
import es.codeurjc13.librored.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class LoanController {

    private final UserService userService;
    private final BookService bookService;
    private final LoanService loanService;

    public LoanController(UserService userService, BookService bookService, LoanService loanService) {
        this.userService = userService;
        this.bookService = bookService;
        this.loanService = loanService;
    }


    @GetMapping("/loans")
    public String listLoans(Model model) {
        List<Loan> loans = loanService.getAllLoans();
        model.addAttribute("loans", loans);
        return "loans";
    }

    @GetMapping("/loans/edit/{id}")
    public String editLoan(@PathVariable Long id, Model model) {
        Loan loan = loanService.getLoanById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        // ✅ Get available books owned by the lender (not in active loans)
        List<Book> availableBooks = bookService.getAvailableBooksByOwnerId(loan.getLender().getId());

        // ✅ Get borrowers, but exclude the lender
        List<User> possibleBorrowers = userService.getAllUsersExcept(loan.getLender());
        possibleBorrowers.remove(loan.getLender()); // Remove lender from borrower list

        model.addAttribute("loan", loan);
        model.addAttribute("books", availableBooks); // Pass only valid books
        model.addAttribute("users", possibleBorrowers); // Pass only valid borrowers
        return "edit-loan";
    }

    @PostMapping("/loans/edit/{id}")
    public String updateLoan(
            @PathVariable Long id,
            @RequestParam(value = "book.id", required = false) Long newBookId,
            @RequestParam(value = "borrower.id", required = false) Long newBorrowerId,
            @RequestParam(value = "startDate", required = false) String newStartDate,
            @RequestParam(value = "endDate", required = false) String newEndDate,
            @RequestParam(value = "status", required = false) String newStatus,
            RedirectAttributes redirectAttributes) {

        Loan loan = loanService.getLoanById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        try {
            if (newBookId != null) {
                Book newBook = bookService.getBookById(newBookId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid book ID"));
                loan.setBook(newBook);
            }

            if (newBorrowerId != null) {
                User newBorrower = userService.getUserById(newBorrowerId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid borrower ID"));
                loan.setBorrower(newBorrower);
            }

            if (newStartDate != null) {
                loan.setStartDate(LocalDate.parse(newStartDate));
            }

            if (newEndDate != null) {
                LocalDate parsedEndDate = LocalDate.parse(newEndDate);
                if (parsedEndDate.isBefore(loan.getStartDate())) {
                    redirectAttributes.addFlashAttribute("error",
                            "End date cannot be before the start date.");
                    return "redirect:/loans/edit/" + id;
                }
                loan.setEndDate(parsedEndDate);
            }

            if (newStatus != null) {
                Loan.Status status = Loan.Status.valueOf(newStatus);
                if (loan.getStatus() == Loan.Status.Completed && status == Loan.Status.Active) {
                    redirectAttributes.addFlashAttribute("error",
                            "You cannot reactivate a completed loan. Create a new loan instead.");
                    return "redirect:/loans/edit/" + id;
                }
                loan.setStatus(status);
            }

            loanService.saveLoan(loan);
            redirectAttributes.addFlashAttribute("message", "Loan updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/loans/edit/" + id;
        }

        return "redirect:/loans";
    }

    @GetMapping("/loans/create")
    public String createLoanForm(Model model) {
        model.addAttribute("loan", new Loan());
        model.addAttribute("users", userService.getAllUsers());  // Load lenders & borrowers
        return "create-loan";
    }

    @PostMapping("/loans/create")
    public String createLoan(@RequestParam Long bookId,
                             @RequestParam Long lenderId,
                             @RequestParam Long borrowerId,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                             @RequestParam Loan.Status status) {

        Book book = bookService.getBookById(bookId).orElseThrow(() -> new IllegalArgumentException("Invalid book ID"));
        User lender = userService.getUserById(lenderId).orElseThrow(() -> new IllegalArgumentException("Invalid lender ID"));
        User borrower = userService.getUserById(borrowerId).orElseThrow(() -> new IllegalArgumentException("Invalid borrower ID"));

        loanService.createLoan(book, lender, borrower, startDate, endDate, status);

        return "redirect:/loans";
    }


    @PostMapping("/loans/delete/{id}")
    public String deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return "redirect:/loans";
    }

    // READ - special needed endpoint
    @GetMapping("/loans/books/{lenderId}")
    @ResponseBody
    public List<Book> getAvailableBooksByLender(@PathVariable Long lenderId) {
        return bookService.getAvailableBooksByOwnerId(lenderId);  // Now filters out books in active loans
    }

}
