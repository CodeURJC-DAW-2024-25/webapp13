package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@Tag(name = "Books", description = "Book management API")
@RestController
@RequestMapping("/api/books")
public class BookRestController {


    private final BookService bookService;


    private final UserService userService;

    public BookRestController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    // Paginated books API
    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(
            @RequestParam(required = false) String title,
            Pageable pageable) {

        Page<Book> books = (title != null && !title.isEmpty())
                ? bookService.findByTitle(title, pageable)
                : bookService.findAllPage(pageable);

        return ResponseEntity.ok(books);
    }

    // Get a single book by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return bookService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //  Create a new book
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        book.setOwner(user);
        Book savedBook = bookService.save(book);

        return ResponseEntity.created(URI.create("/api/books/" + savedBook.getId())).body(savedBook);
    }

    //  Update an existing book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @RequestBody Book updatedBook,
            Authentication authentication) {

        if (!bookService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (!bookService.isOwnerOrAdmin(id, authentication.getName(), authentication.getAuthorities())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // keep the original owner
        Book existingBook = bookService.findBookById(id);
        updatedBook.setId(id);
        updatedBook.setOwner(existingBook.getOwner());

        return ResponseEntity.ok(bookService.save(updatedBook));
    }

    //  Delete a book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, Authentication authentication) {
        if (!bookService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (!bookService.isOwnerOrAdmin(id, authentication.getName(), authentication.getAuthorities())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Books per genre (for graphs or analytics)
    @GetMapping("/books-per-genre")
    public ResponseEntity<Map<String, Long>> getBooksPerGenre() {
        return ResponseEntity.ok(bookService.getBooksPerGenre());
    }

    // Search books by title, author or genre
    @GetMapping("/search")
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            Pageable pageable) {

        return ResponseEntity.ok(
                bookService.searchBooks(title, author, genre, pageable)
        );
    }

    // API Image Endpoint related methods

    // Get the cover of a book by id
    @GetMapping("/{id}/cover")
    public ResponseEntity<Resource> getBookCover(@PathVariable Long id) {
        try {
            Book book = bookService.findBookById(id);

            if (book.getCoverPicFile() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resource file = new InputStreamResource(book.getCoverPicFile().getBinaryStream());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(file);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/cover")
    public ResponseEntity<Object> createBookImage(
            @PathVariable long id,
            @RequestParam MultipartFile imageFile) throws IOException {

        URI location = fromCurrentRequest().build().toUri();
        bookService.createBookImage(id, location, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}/cover")
    public ResponseEntity<Object> replaceBookImage(
            @PathVariable long id,
            @RequestParam MultipartFile imageFile) throws IOException {

        bookService.replaceBookImage(id, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/cover")
    public ResponseEntity<Object> deleteBookImage(@PathVariable long id) {
        bookService.deleteBookImage(id);
        return ResponseEntity.noContent().build();
    }

}
