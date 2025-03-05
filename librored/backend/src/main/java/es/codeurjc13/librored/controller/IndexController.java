package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.repository.BookRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    private final BookRepository bookRepository;

    public IndexController(BookRepository bookRepository) {

        this.bookRepository = bookRepository;
    }

    @GetMapping("/")
    public String index(Model model) {

        // Fetch books from the database
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books); // Ensure books list is passed to the view
        return "index";
    }

}
