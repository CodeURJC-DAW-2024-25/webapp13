package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.repository.BookRepository;
import es.codeurjc13.librored.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<String> getBooks(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "8") int size) {
        List<Book> books = bookService.getBooks(page, size);

        if (books.isEmpty()) {
            return ResponseEntity.ok(""); // Return empty string if no books are left
        }

        // Convert books to HTML snippets
        StringBuilder htmlResponse = new StringBuilder();
        for (Book book : books) {
            htmlResponse.append(
                    "<li class='col-xl-3 col-lg-4 col-sm-6 col-12 book-item' style='display: block;'>"
                            + "    <div class='product-wrap'>"
                            + "        <div class='product-img'>"
                            + "            <img src='" + book.getCover_pic() + "' alt='" + book.getTitle() + "'>"
                            + "        </div>"
                            + "        <div class='product-content'>"
                            + "            <h3><a href='single-product.html'>" + book.getTitle() + "</a></h3>"
                            + "            <p class='pull-left'>" + book.getAuthor() + "</p>"
                            + "        </div>"
                            + "    </div>"
                            + "</li>"
            );
        }

        return ResponseEntity.ok(htmlResponse.toString());
    }
}
