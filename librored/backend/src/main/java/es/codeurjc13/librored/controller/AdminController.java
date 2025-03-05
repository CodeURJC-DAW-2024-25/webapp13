package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.LoanService;
import es.codeurjc13.librored.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/admin")
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


    // PUEDE SER QUE ESTE SEA EL ÚNICO QUE SE SALVE
    @GetMapping("/download-report")
    public void downloadReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Admin_Report.pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Add Title
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(200, 750);
            contentStream.showText("ADMIN REPORT");
            contentStream.endText();

            // Section Titles Styling
            int y = 720; // Y position
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);

            // Add Users Table
            y = addSectionHeader(contentStream, "Users List", y);
            y = addUserTable(contentStream, y);

            // Add Books Table
            y = addSectionHeader(contentStream, "Books List", y);
            y = addBookTable(contentStream, y);

            // Add Loans Table
            y = addSectionHeader(contentStream, "Loans List", y);
            addLoanTable(contentStream, y);

            contentStream.close();
            document.save(response.getOutputStream());
        }
    }

    // 🔹 Section Headers Helper
    private int addSectionHeader(PDPageContentStream contentStream, String title, int y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.newLineAtOffset(50, y);
        contentStream.showText(title);
        contentStream.endText();
        return y - 20;
    }

    // 🔹 Users Table
    private int addUserTable(PDPageContentStream contentStream, int y) throws IOException {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, y);
            contentStream.showText("ID" + user.getId() + " | Username " + user.getUsername() + " | Email " + user.getEmail());
            contentStream.endText();
            y -= 15;
        }
        return y - 10;
    }

    // 🔹 Books Table
    private int addBookTable(PDPageContentStream contentStream, int y) throws IOException {
        List<Book> books = bookService.getAllBooks();
        for (Book book : books) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, y);
            contentStream.showText("ID" + book.getId() + " | Title " + book.getTitle() + " | Author " + book.getAuthor() + " | Genre " + book.getGenre());
            contentStream.endText();
            y -= 15;
        }
        return y - 10;
    }

    // 🔹 Loans Table
    private void addLoanTable(PDPageContentStream contentStream, int y) throws IOException {
        List<Loan> loans = loanService.getAllLoans();
        for (Loan loan : loans) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, y);
            contentStream.showText("Loan ID: " + loan.getId() + " | Book: " + loan.getBook().getTitle() + " | Lender: " + loan.getLender().getUsername());
            contentStream.endText();
            y -= 15;
        }
    }

}
