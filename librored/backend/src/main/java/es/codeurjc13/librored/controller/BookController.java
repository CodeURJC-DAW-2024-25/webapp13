package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "https://localhost:8443")
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

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
}

