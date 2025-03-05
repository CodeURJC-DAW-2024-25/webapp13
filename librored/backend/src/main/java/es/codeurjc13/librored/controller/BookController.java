package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;


@Controller
//@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {

            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));

        } else {
            model.addAttribute("logged", false);
        }
    }


    @GetMapping("/")
    public String index(Model model) {

        // Fetch books from the database
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books); // Ensure books list is passed to the view
        return "index";
    }

    // We use this for AJAX pagination
    @GetMapping
    public ResponseEntity<List<Book>> getBooks(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "8") int size) {
        List<Book> books = bookService.getBooks(page, size);

        return ResponseEntity.ok(books);
    }


    // when we need all the books
    @GetMapping("/books")
    public String listBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }


    // CREATE
    @GetMapping("/books/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("genres", Book.Genre.values());  // Pass genres to the template
        return "create-book";
    }


    @PostMapping("/books/create")
    public String createBook(@RequestParam String title,
                             @RequestParam String author,
                             @RequestParam Book.Genre genre,
                             @RequestParam String description,
                             @RequestParam("coverImage") MultipartFile coverImage,
                             @RequestParam Long ownerId) {

        User owner = userService.getUserById(ownerId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        String coverPicPath = saveImage(coverImage);  // Save image to server

        bookService.createBook(title, author, description, coverPicPath, genre, owner);


        return "redirect:/books";
    }

    // EDIT
    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            model.addAttribute("users", userService.getAllUsers()); // Pass users for owner selection
            model.addAttribute("genres", Book.Genre.values());  // Pass genres to the template
            return "edit-book";
        }
        return "redirect:/books";
    }

    @PostMapping("/books/edit/{id}")
    public String updateBook(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String author,
                             @RequestParam Book.Genre genre,
                             @RequestParam String description,
                             @RequestParam(required = false) MultipartFile coverPic,
                             @RequestParam Long ownerId) {

        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book ID"));

        User owner = userService.getUserById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setDescription(description);
        book.setOwner(owner);

        if (coverPic != null && !coverPic.isEmpty()) {
            String coverPicPath = saveImage(coverPic);
            book.setCoverPic(coverPicPath);
        }

        bookService.saveBook(book);
        return "redirect:/books";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }

    // READ - graph needed endpoint
    @GetMapping("/books-per-genre")
    public ResponseEntity<Map<String, Long>> getBooksPerGenre() {
        return ResponseEntity.ok(bookService.getBooksPerGenre());
    }


    // Save the uploaded file to /static/uploads/
    private String saveImage(MultipartFile file) {
        try {
            String uploadDir = "src/main/resources/static/images/covers/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            return "images/covers/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }


}
