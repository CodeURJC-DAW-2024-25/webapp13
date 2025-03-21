package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Books", description = "Book management API")
@RestController
@RequestMapping("/api/books")
public class BookRestController {

    @Autowired
    private final BookService bookService;

    @Autowired
    private final UserService userService;

    public BookRestController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    // Get the cover of a book by id
    @GetMapping("/{id}/cover")
    public ResponseEntity<Resource> getBookCover(@PathVariable Long id) {
        try {
            Book book = bookService.findBookById(id);

            if (book.getCoverPic() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resource file = new InputStreamResource(book.getCoverPic().getBinaryStream());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(file);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.save(book);
        return ResponseEntity.created(URI.create("/api/books/" + savedBook.getId())).body(savedBook);
    }

    //  Update an existing book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        if (!bookService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        book.setId(id);
        return ResponseEntity.ok(bookService.save(book));
    }

    //  Delete a book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Books per genre (for graphs or analytics)
    @GetMapping("/books-per-genre")
    public ResponseEntity<Map<String, Long>> getBooksPerGenre() {
        return ResponseEntity.ok(bookService.getBooksPerGenre());
    }
}
