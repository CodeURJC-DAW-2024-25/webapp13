package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Page<Book> books = bookService.getBooks(page, size);

        // Convert Page<Book> to a stable JSON format
        Map<String, Object> response = new HashMap<>();
        response.put("content", books.getContent());  // List of books
        response.put("currentPage", books.getNumber());
        response.put("totalPages", books.getTotalPages());
        response.put("totalItems", books.getTotalElements());
        response.put("last", books.isLast());

        return ResponseEntity.ok(response);
    }


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


    // Books per genre (for graphs or analytics)
    @GetMapping("/books-per-genre")
    public ResponseEntity<Map<String, Long>> getBooksPerGenre() {
        return ResponseEntity.ok(bookService.getBooksPerGenre());
    }
}
