package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.SQLException;
import java.util.*;


@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {

            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));

        } else {
            model.addAttribute("logged", false);
        }
    }


    @GetMapping("/")
    public String index(Model model) {

        // Fetch books from the database
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books); // Ensure books list is passed to the view
        return "index";
    }

    // Render the books.mustache template with all books
    @GetMapping("/books")
    public String listBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }


    // CREATE
    @GetMapping("/books/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("genres", Book.Genre.values());  // Pass genres to the template
        return "create-book";
    }


    @PostMapping("/books/create")
    public String createBook(@RequestParam String title,
                             @RequestParam String author,
                             @RequestParam Book.Genre genre,
                             @RequestParam String description,
                             @RequestParam("coverImage") MultipartFile coverImage,
                             @RequestParam Long ownerId) {

        User owner = userService.getUserById(ownerId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        //String coverPicPath = saveImage(coverImage);  // Save image to server

        bookService.createBook(title, author, description, genre, owner);


        return "redirect:/books";
    }

    // PAINT IMAGE FROM DB
    @GetMapping("/books/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent() && book.get().getCoverPic() != null) {

            Resource file = new InputStreamResource(book.get().getCoverPic().getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(book.get().getCoverPic().length()).body(file);

        } else {
            return ResponseEntity.notFound().build();
        }
    }


    //private void setCoverPic(Book book, boolean removeImage, MultipartFile imageField) throws IOException, SQLException {
    private void setCoverPic(Book book, MultipartFile imageField) throws IOException, SQLException {

        if (!imageField.isEmpty()) {
            book.setCoverPic(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
            //book.setImage(true);
        }
//        else {
//            if (removeImage) {
//                book.setCoverPic(null);
//                //book.setImage(false);
//            }
/*            else {
                // Maintain the same image loading it before updating the book
                Book dbBook = bookService.findById(book.getId()).orElseThrow();
                if (dbBook.getCoverPic()) {
                    book.setImageFile(BlobProxy.generateProxy(dbBook.getImageFile().getBinaryStream(),
                            dbBook.getImageFile().length()));
                    book.setImage(true);
                }
            }*/
        //}
    }


    // EDIT
    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            model.addAttribute("users", userService.getAllUsers()); // Pass users for owner selection
            model.addAttribute("genres", Book.Genre.values());  // Pass genres to the template
            return "edit-book";
        }
        return "redirect:/books";
    }

    @PostMapping("/books/edit/{id}")
    public String updateBook(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String author,
                             @RequestParam Book.Genre genre,
                             @RequestParam String description,
                             @RequestParam(required = false) MultipartFile coverPic,
                             @RequestParam Long ownerId) throws SQLException, IOException {

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
            //String coverPicPath = setCoverPic(coverPic);
            setCoverPic(book, coverPic);
        }

        bookService.saveBook(book);
        return "redirect:/books";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }


    // Save the uploaded file to /static/uploads/
    // Saves in local
    private String saveImage(MultipartFile file) {
        try {
            String uploadDir = "src/main/resources/static/images/covers/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            return "images/covers/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }


}
