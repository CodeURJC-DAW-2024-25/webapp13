package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.LoanService;
import es.codeurjc13.librored.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final BookService bookService;
    private final LoanService loanService;

    public AdminController(UserService userService, BookService bookService, LoanService loanService) {
        this.userService = userService;
        this.bookService = bookService;
        this.loanService = loanService;
    }

    @GetMapping
    public String adminDashboard() {
        return "admin";
    }

    // ✅ Users CRUD

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "admin/edit-user";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User updatedUser) {
        userService.updateUser(id, updatedUser);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/create-user";  // New Mustache template for user creation
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user) {
        userService.registerUser(user);  // Ensure password encoding
        return "redirect:/admin/users";
    }


    // ✅ Books CRUD

    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "admin/books";
    }

    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            model.addAttribute("users", userService.getAllUsers()); // ✅ Pass users for owner selection
            return "admin/edit-book";
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/books/edit/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book) {
        bookService.updateBook(id, book);
        return "redirect:/admin/books";
    }

    @GetMapping("/books/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("genres", Book.Genre.values());
        return "admin/create-book";  // ✅ Must match the Mustache file name
    }


    @PostMapping("/books/create")
    public String createBook(@RequestParam String title,
                             @RequestParam String author,
                             @RequestParam Book.Genre genre,
                             @RequestParam String description,
                             @RequestParam("coverImage") MultipartFile coverImage,
                             @RequestParam Long ownerId) {

        User owner = userService.getUserById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        String coverPicPath = saveImage(coverImage);  // ✅ Save image to server

        bookService.createBook(title, author, description, coverPicPath, genre, owner);

        return "redirect:/admin/books";
    }

    // ✅ Save uploaded file to local directory
    private String saveImage(MultipartFile file) {
        try {
            String uploadDir = "src/main/resources/static/uploads/";
            Files.createDirectories(Paths.get(uploadDir));  // Ensure directory exists

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            return "/uploads/" + fileName;  // ✅ Return relative path to be stored in DB
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/admin/books";
    }

    // ✅ Loans CRUD

    @GetMapping("/loans")
    public String listLoans(Model model) {
        List<Loan> loans = loanService.getAllLoans();
        model.addAttribute("loans", loans);
        return "admin/loans";
    }

    @GetMapping("/loans/edit/{id}")
    public String editLoanForm(@PathVariable Long id, Model model) {
        Optional<Loan> loan = loanService.getLoanById(id);
        if (loan.isPresent()) {
            model.addAttribute("loan", loan.get());
            model.addAttribute("books", bookService.getAllBooks());
            model.addAttribute("users", userService.getAllUsers());
            return "admin/edit-loan";
        }
        return "redirect:/admin/loans";
    }

    @PostMapping("/loans/edit/{id}")
    public String updateLoan(@PathVariable Long id, @ModelAttribute Loan loan) {
        loanService.updateLoan(id, loan);
        return "redirect:/admin/loans";
    }

    @GetMapping("/loans/create")
    public String createLoanForm(Model model) {
        model.addAttribute("loan", new Loan());
        model.addAttribute("users", userService.getAllUsers());  // Load lenders & borrowers
        return "admin/create-loan";
    }


    @GetMapping("/loans/books/{lenderId}")
    @ResponseBody
    public List<Book> getAvailableBooksByLender(@PathVariable Long lenderId) {
        return bookService.getAvailableBooksByOwnerId(lenderId);  // Now filters out books in active loans
    }


    @PostMapping("/loans/create")
    public String createLoan(@RequestParam Long bookId,
                             @RequestParam Long lenderId,
                             @RequestParam Long borrowerId,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                             @RequestParam Loan.Status status) {

        Book book = bookService.getBookById(bookId).orElseThrow(() -> new IllegalArgumentException("Invalid book ID"));
        User lender = userService.getUserById(lenderId).orElseThrow(() -> new IllegalArgumentException("Invalid lender ID"));
        User borrower = userService.getUserById(borrowerId).orElseThrow(() -> new IllegalArgumentException("Invalid borrower ID"));

        loanService.createLoan(book, lender, borrower, startDate, endDate, status);

        return "redirect:/admin/loans";
    }


    @PostMapping("/loans/delete/{id}")
    public String deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return "redirect:/admin/loans";
    }
}
