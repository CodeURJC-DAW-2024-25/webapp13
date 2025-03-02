package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.repository.BookRepository;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class IndexController {

    private final BookRepository bookRepository;

    public IndexController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));
        } else {
            model.addAttribute("logged", false);
        }

        // Fetch books from the database
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books); // Ensure books list is passed

        // ✅ Ensure CSRF token is always available
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("csrf_token", csrfToken.getToken());
            model.addAttribute("csrf_parameter", csrfToken.getParameterName());
        } else {
            model.addAttribute("csrf_token", ""); // Prevent Mustache errors
            model.addAttribute("csrf_parameter", "_csrf");
        }

        return "index";
    }

    private void addNameAttribute(Model model, String name) {
        model.addAttribute("name", name);
    }

    private void addNameAttribute(Model model, String author, String title) {
        model.addAttribute("author", author);
        model.addAttribute("title", title);
    }


}
