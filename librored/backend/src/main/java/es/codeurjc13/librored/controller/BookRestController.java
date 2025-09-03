package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.dto.BookDTO;
import es.codeurjc13.librored.dto.BookBasicDTO;
import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Books", description = "Book management API")
public class BookRestController {

    private final BookService bookService;
    private final UserService userService;

    public BookRestController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    // ==================== EXISTING WEB APP ENDPOINTS (/api/books) ====================
    
    // Paginated books API - CRITICAL for web app functionality
    @GetMapping("/api/books")
    public ResponseEntity<Map<String, Object>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Page<Book> books = bookService.getBooks(page, size);

        // Convert Page<Book> to a stable JSON format with DTOs to avoid lazy loading issues
        Map<String, Object> response = new HashMap<>();
        response.put("content", books.getContent().stream()
                .map(book -> {
                    Map<String, Object> bookMap = new HashMap<>();
                    bookMap.put("id", book.getId());
                    bookMap.put("title", book.getTitle());
                    bookMap.put("author", book.getAuthor());
                    bookMap.put("genre", book.getGenre());
                    bookMap.put("description", book.getDescription());
                    bookMap.put("hasCoverImage", book.getCoverPic() != null);
                    if (book.getOwner() != null) {
                        Map<String, Object> ownerMap = new HashMap<>();
                        ownerMap.put("id", book.getOwner().getId());
                        ownerMap.put("username", book.getOwner().getUsername());
                        bookMap.put("owner", ownerMap);
                    } else {
                        bookMap.put("owner", null);
                    }
                    return bookMap;
                })
                .toList());
        response.put("currentPage", books.getNumber());
        response.put("totalPages", books.getTotalPages());
        response.put("totalItems", books.getTotalElements());
        response.put("last", books.isLast());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/api/books/{id}/cover")
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
    @GetMapping("/api/books/books-per-genre")
    public ResponseEntity<Map<String, Long>> getBooksPerGenre() {
        return ResponseEntity.ok(bookService.getBooksPerGenre());
    }

    // ==================== P2 REST API ENDPOINTS (/api/v1/books) ====================

    @Operation(summary = "Get all books", description = "Retrieve a list of all books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/v1/books")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooksDTO();
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/api/v1/books/{id}")
    public ResponseEntity<BookDTO> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        Optional<BookDTO> book = bookService.getBookByIdDTO(id);
        return book.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new book", description = "Create a new book entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid book data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/api/v1/books")
    public ResponseEntity<BookDTO> createBook(
            @Parameter(description = "Book data") @Valid @RequestBody BookDTO bookDTO) {
        try {
            BookDTO createdBook = bookService.createBookDTO(bookDTO);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdBook.id())
                    .toUri();
            return ResponseEntity.created(location).body(createdBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update book", description = "Update an existing book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid book data")
    })
    @PutMapping("/api/v1/books/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "Updated book data") @Valid @RequestBody BookDTO bookDTO) {
        Optional<BookDTO> updatedBook = bookService.updateBookDTO(id, bookDTO);
        return updatedBook.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete book", description = "Delete a book by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/api/v1/books/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        boolean deleted = bookService.deleteBookDTO(id);
        return deleted ? ResponseEntity.noContent().build() 
                       : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get books by owner", description = "Retrieve books owned by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    @GetMapping("/api/v1/books/owner/{ownerId}")
    public ResponseEntity<List<BookDTO>> getBooksByOwner(
            @Parameter(description = "Owner ID") @PathVariable Long ownerId) {
        List<BookDTO> books = bookService.getBooksByOwnerIdDTO(ownerId);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Get available books by owner", description = "Get available books for lending by owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available books retrieved"),
            @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    @GetMapping("/api/v1/books/available/{ownerId}")
    public ResponseEntity<List<BookBasicDTO>> getAvailableBooksByOwner(
            @Parameter(description = "Owner ID") @PathVariable Long ownerId) {
        List<BookBasicDTO> books = bookService.getAvailableBooksByOwnerIdDTO(ownerId);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Get book recommendations", description = "Get book recommendations for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommendations retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/api/v1/books/recommendations/{userId}")
    public ResponseEntity<List<BookDTO>> getRecommendations(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<BookDTO> recommendations = bookService.getRecommendationsForUserDTO(userId);
        return ResponseEntity.ok(recommendations);
    }

    @Operation(summary = "Get books per genre statistics", description = "Get count of books grouped by genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    @GetMapping("/api/v1/books/stats/genre")
    public ResponseEntity<Map<String, Long>> getBooksPerGenreStats() {
        Map<String, Long> stats = bookService.getBooksPerGenreDTO();
        return ResponseEntity.ok(stats);
    }

}
