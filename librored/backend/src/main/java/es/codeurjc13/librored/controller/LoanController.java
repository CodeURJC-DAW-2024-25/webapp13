package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.LoanService;
import es.codeurjc13.librored.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

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
    public String listLoans(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRole() == User.Role.ROLE_ADMIN; // Correct way to check admin role


        List<Loan> loans = isAdmin ? loanService.getAllLoans() : loanService.getLoansByLender(user);

        model.addAttribute("loans", loans);
        model.addAttribute("isAdmin", isAdmin);

        return "loans";  // ✅ Reuses loans.html for both users and admins
    }

    @GetMapping("/loans/edit/{id}")
    public String editLoan(@PathVariable Long id, Model model) {
        Loan loan = loanService.getLoanById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        // ✅ Get available books owned by the lender (not currently loaned)
        List<Book> availableBooks = bookService.getAvailableBooksByOwnerId(loan.getLender().getId());

        // ✅ Get valid borrowers (excluding the lender)
        List<User> possibleBorrowers = userService.getAllUsersExcept(loan.getLender());

        // ✅ Convert loan status to string format
        String formattedStatus = loan.getStatus().name();

        // ✅ Ensure endDate is formatted properly for Mustache
        String formattedEndDate = (loan.getEndDate() != null) ? loan.getEndDate().toString() : "";

        // 🔥 Pass necessary attributes to Mustache
        model.addAttribute("loan", loan);
        model.addAttribute("books", availableBooks);  // ✅ Pass books list
        model.addAttribute("users", possibleBorrowers);  // ✅ Pass users list
        model.addAttribute("formattedStatus", formattedStatus);
        model.addAttribute("isStatusActive", "Active".equals(formattedStatus));
        model.addAttribute("isStatusCompleted", "Completed".equals(formattedStatus));
        model.addAttribute("loanEndDate", formattedEndDate);

        System.out.println("📌 DEBUG: Available Books: " + availableBooks.size());
        System.out.println("📌 DEBUG: Possible Borrowers: " + possibleBorrowers.size());


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

            if (newEndDate != null && !newEndDate.isEmpty()) {  // Prevents parsing errors
                LocalDate parsedEndDate = LocalDate.parse(newEndDate);
                if (parsedEndDate.isBefore(loan.getStartDate())) {
                    redirectAttributes.addFlashAttribute("error",
                            "End date cannot be before the start date.");
                    return "redirect:/loans/edit/" + id;
                }
                loan.setEndDate(parsedEndDate);
            } else {
                loan.setEndDate(null);  // Ensures null values are correctly stored
            }

            if (newStatus != null) {
                try {
                    //  Handle case sensitivity properly: Accepts "Active" and "Completed"
                    Loan.Status status = newStatus.equalsIgnoreCase("Active") ? Loan.Status.Active : Loan.Status.Completed;

                    //  Prevent reactivation of completed loans
                    if (loan.getStatus() == Loan.Status.Completed && status == Loan.Status.Active) {
                        redirectAttributes.addFlashAttribute("error",
                                "You cannot reactivate a completed loan. Create a new loan instead.");
                        return "redirect:/loans/edit/" + id;
                    }

                    loan.setStatus(status);
                } catch (IllegalArgumentException e) {
                    redirectAttributes.addFlashAttribute("error", "Invalid status value.");
                    return "redirect:/loans/edit/" + id;
                }
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
    public String createLoanForm(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User lender = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = lender.getRole() == User.Role.ROLE_ADMIN; // Correct way to check admin role


        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("userId", lender.getId()); // Pass the logged-in user's ID
        model.addAttribute("loan", new Loan());
        
        if (isAdmin) {
            // For admin, don't preload books - they will be loaded via AJAX when lender is selected
            model.addAttribute("books", List.of()); // Empty list for admin
            model.addAttribute("users", userService.getAllUsers()); // Load all users for admin selection
        } else {
            // For regular users, load their own books
            model.addAttribute("books", bookService.getAvailableBooksByOwnerId(lender.getId()));
        }

        return "create-loan";
    }


    @PostMapping("/loans/create")
    public String createLoan(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
                             @RequestParam Long bookId,
                             @RequestParam Long lenderId,
                             @RequestParam Long borrowerId,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                             @RequestParam Loan.Status status,
                             RedirectAttributes redirectAttributes) {

        try {
            // Get current user
            User currentUser = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            boolean isAdmin = currentUser.getRole() == User.Role.ROLE_ADMIN;
            
            // For regular users, force them to be the lender
            if (!isAdmin) {
                lenderId = currentUser.getId();
            }

            Book book = bookService.getBookById(bookId).orElseThrow(() -> new IllegalArgumentException("Invalid book ID"));
            User lender = userService.getUserById(lenderId).orElseThrow(() -> new IllegalArgumentException("Invalid lender ID"));
            User borrower = userService.getUserById(borrowerId).orElseThrow(() -> new IllegalArgumentException("Invalid borrower ID"));

            // Validation 1: Lender and borrower cannot be the same person
            if (lender.getId().equals(borrower.getId())) {
                redirectAttributes.addFlashAttribute("error", "Lender and borrower cannot be the same person.");
                return "redirect:/loans/create";
            }

            // Validation 2: Start date must be today or future
            LocalDate today = LocalDate.now();
            if (startDate.isBefore(today)) {
                redirectAttributes.addFlashAttribute("error", "Start date must be today or in the future.");
                return "redirect:/loans/create";
            }

            // Validation 3: End date (if provided) must be after today
            if (endDate != null && !endDate.isAfter(today)) {
                redirectAttributes.addFlashAttribute("error", "End date must be in the future.");
                return "redirect:/loans/create";
            }

            // Validation 4: End date must be after start date
            if (endDate != null && !endDate.isAfter(startDate)) {
                redirectAttributes.addFlashAttribute("error", "End date must be after start date.");
                return "redirect:/loans/create";
            }

            // Validation 5: Book must be owned by the lender
            if (!book.getOwner().getId().equals(lender.getId())) {
                redirectAttributes.addFlashAttribute("error", "The selected book is not owned by the lender.");
                return "redirect:/loans/create";
            }

            loanService.createLoan(book, lender, borrower, startDate, endDate, status);
            redirectAttributes.addFlashAttribute("message", "Loan created successfully!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/loans/create";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while creating the loan.");
            return "redirect:/loans/create";
        }

        return "redirect:/loans";
    }


    @PostMapping("/loans/delete/{id}")
    public String deleteLoan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        loanService.deleteLoan(id);
        redirectAttributes.addFlashAttribute("message", "Loan successfully deleted.");
        return "redirect:/loans";
    }

    // READ - special needed endpoint
    @GetMapping("/loans/books/{lenderId}")
    @ResponseBody
    public List<Book> getAvailableBooksByLender(@PathVariable Long lenderId) {
        //System.out.println("📚 DEBUG: Available books size = " + availableBooks.size());
        System.out.println("\uD83D\uDCDA DEBUG: available books for lender " + lenderId);
        List<Book> books = bookService.getAvailableBooksByOwnerId(lenderId);
        System.out.println("🔍 DEBUG: Found " + books.size() + " available books for lender " + lenderId);
        return books;
    }

}
