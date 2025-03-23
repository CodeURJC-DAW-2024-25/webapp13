package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.dto.BookCreateDTO;
import es.codeurjc13.librored.dto.BookDTO;
import es.codeurjc13.librored.dto.BookUpdateDTO;
import es.codeurjc13.librored.mapper.BookMapper;
import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Map;

@Tag(name = "Books", description = "Book management API")
@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService bookService;
    private final UserService userService;
    private final BookMapper bookMapper;

    public BookRestController(BookService bookService, UserService userService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.userService = userService;
        this.bookMapper = bookMapper;
    }

    @Operation(summary = "Get all books", description = "Returns a paginated list of books with optional filtering by title, author, or genre.")
    @ApiResponse(responseCode = "200", description = "List of books returned")
    @GetMapping
    public ResponseEntity<Page<BookDTO>> getAllBooks(
            @RequestParam(required = false) String title,
            Pageable pageable) {

        Page<Book> books = (title != null && !title.isEmpty())
                ? bookService.findByTitle(title, pageable)
                : bookService.findAllPage(pageable);

        return ResponseEntity.ok(books.map(bookMapper::toDTO));
    }

    @Operation(summary = "Get book by ID", description = "Returns a book by its ID")
    @ApiResponse(responseCode = "200", description = "Book found", content = @Content(schema = @Schema(implementation = BookDTO.class)))
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        return bookService.findById(id)
                .map(bookMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new book", description = "Creates a new book with the current authenticated user as owner")
    @ApiResponse(responseCode = "201", description = "Book created")
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookCreateDTO bookDTO, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        Book savedBook = bookService.createBook(bookDTO, user);
        URI location = URI.create("/api/books/" + savedBook.getId());
        return ResponseEntity.created(location).body(bookMapper.toDTO(savedBook));
    }

    @Operation(summary = "Update book", description = "Updates a book's details")
    @ApiResponse(responseCode = "200", description = "Book updated")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @RequestBody BookUpdateDTO dto,
            Authentication authentication) {

        if (!bookService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (!bookService.isOwnerOrAdmin(id, authentication.getName(), authentication.getAuthorities())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Book updatedBook = bookService.updateBook(id, dto);
        return ResponseEntity.ok(bookMapper.toDTO(updatedBook));
    }

    @Operation(summary = "Delete book", description = "Deletes a book by its ID")
    @ApiResponse(responseCode = "204", description = "Book deleted")
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

    @Operation(summary = "Books per genre", description = "Returns the number of books grouped by genre")
    @ApiResponse(responseCode = "200", description = "Genre statistics returned")
    @GetMapping("/books-per-genre")
    public ResponseEntity<Map<String, Long>> getBooksPerGenre() {
        return ResponseEntity.ok(bookService.getBooksPerGenre());
    }

    @Operation(summary = "Search books", description = "Searches books by title, author, or genre")
    @ApiResponse(responseCode = "200", description = "Books found")
    @ApiResponse(responseCode = "404", description = "Books not found")
    @GetMapping("/search")
    public ResponseEntity<Page<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            Pageable pageable) {

        Page<Book> books = bookService.searchBooks(title, author, genre, pageable);
        return ResponseEntity.ok(books.map(bookMapper::toDTO));
    }

    // ---------- Cover Image Endpoints ----------

    @Operation(summary = "Upload book cover", description = "Uploads a cover image for a book")
    @ApiResponse(responseCode = "200", description = "Image uploaded")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @ApiResponse(responseCode = "500", description = "Error uploading image")
    @PostMapping("/{id}/cover")
    public ResponseEntity<Void> uploadBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        try {
            Blob coverBlob = new SerialBlob(file.getBytes());
            book.setCoverPic(coverBlob);
            bookService.save(book);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error uploading image", e);
        }
    }

    @Operation(summary = "Download book cover", description = "Downloads the cover image of a book")
    @ApiResponse(responseCode = "200", description = "Image found")
    @ApiResponse(responseCode = "404", description = "Book or image not found")
    @GetMapping("/{id}/cover")
    public ResponseEntity<Resource> downloadCover(@PathVariable long id) throws SQLException {
        Resource image = bookService.getBookImage(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpg")
                .body(image);
    }

    @Operation(summary = "Replace book cover", description = "Replaces the cover image of a book")
    @ApiResponse(responseCode = "204", description = "Image replaced")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @PutMapping("/{id}/cover")
    public ResponseEntity<Object> replaceCover(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
        bookService.replaceBookImage(id, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete book cover", description = "Deletes the cover image of a book")
    @ApiResponse(responseCode = "204", description = "Image deleted")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @DeleteMapping("/{id}/cover")
    public ResponseEntity<Object> deleteCover(@PathVariable long id) {
        bookService.deleteBookImage(id);
        return ResponseEntity.noContent().build();
    }
}
