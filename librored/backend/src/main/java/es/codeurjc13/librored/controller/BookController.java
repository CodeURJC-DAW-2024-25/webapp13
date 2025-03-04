package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/admin/books/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        Optional<Book> bookOpt = bookService.getBookById(id);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            System.out.println("DEBUG: Book -> " + book);
            model.addAttribute("book", book);

            // Pass all genre values and mark the selected one
            List<Map<String, Object>> genres = Arrays.stream(Book.Genre.values())
                    .map(genre -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("genre", genre);
                        map.put("selected", genre.equals(book.getGenre()));
                        return map;
                    })
                    .collect(Collectors.toList());

            model.addAttribute("genres", genres);
            return "admin/edit-book";
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/admin/books/edit/{id}")
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
        return "redirect:/admin/books";
    }

    // Save the uploaded file to /static/uploads/
    private String saveImage(MultipartFile file) {
        try {
            String uploadDir = "src/main/resources/static/uploads/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            return "/uploads/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }


}
