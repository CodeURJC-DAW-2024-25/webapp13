package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "https://localhost:8443")
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Book>> getBooks(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "8") int size) {
        List<Book> books = bookService.getBooks(page, size);

        return ResponseEntity.ok(books);
    }

    @GetMapping("/books-per-genre")
    public ResponseEntity<Map<String, Long>> getBooksPerGenre() {
        return ResponseEntity.ok(bookService.getBooksPerGenre());
    }

    @GetMapping("/books/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("genres", Book.Genre.values());  // Pass genres to the template
        return "admin/create-book";
    }


    @PostMapping("/books/create")
    public String createBook(@RequestParam String title,
                             @RequestParam String author,
                             @RequestParam Book.Genre genre,
                             @RequestParam String description,
                             @RequestParam String coverPic,
                             @RequestParam Long ownerId) {

        User owner = userService.getUserById(ownerId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        bookService.createBook(title, author, description, coverPic, genre, owner);

        return "redirect:/admin/books";
    }
}
