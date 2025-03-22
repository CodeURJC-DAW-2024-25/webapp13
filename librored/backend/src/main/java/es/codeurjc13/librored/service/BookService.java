package es.codeurjc13.librored.service;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.BookRepository;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Page<Book> getBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable);
    }

    // for the api endpoint @GetMapping("/{id}/cover")
    public Book findBookById(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        return bookOptional.orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    public Map<String, Long> getBooksPerGenre() {
        List<Object[]> results = bookRepository.countBooksByGenre();
        Map<String, Long> booksPerGenre = new HashMap<>();

        for (Object[] result : results) {
            Book.Genre genreEnum = (Book.Genre) result[0]; // Cast Enum correctly
            String genre = genreEnum.name(); // Convert Enum to String

            Long count = (Long) result[1];
            booksPerGenre.put(genre, count);
        }

        return booksPerGenre;
    }

    public List<Book> getAvailableBooksByOwnerId(Long ownerId) {
        return bookRepository.findAvailableBooksByOwnerId(ownerId);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> getBooksByOwner(User owner) {
        return bookRepository.findByOwner(owner);
    }

    public List<Book> getRecommendationsForUser(Long userId) {
        return bookRepository.findRecommendedBooks(userId);
    }

    // Find books by title with pagination
    public Page<Book> findByTitle(String title, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    // Find a book by ID
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    // Save a new or existing book
    public Book save(Book book) {
        Book saved = bookRepository.save(book);

        if (saved.getCoverPicUrl() == null || saved.getCoverPicUrl().isEmpty()) {
            saved.setCoverPicUrl("/api/books/" + saved.getId() + "/cover");
            saved = bookRepository.save(saved); // Save again to persist URL
        }

        return saved;
    }


    // Check if a book exists by ID
    public boolean existsById(Long id) {
        return !bookRepository.existsById(id);
    }

    // Delete a book by ID
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    public Page<Book> findAllPage(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Page<Book> searchBooks(String title, String author, String genre, Pageable pageable) {
        return bookRepository.searchBooks(
                (title != null && !title.isEmpty()) ? title : null,
                (author != null && !author.isEmpty()) ? author : null,
                (genre != null && !genre.isEmpty()) ? genre : null,
                pageable
        );
    }

    // Image Endpoint related methods
    public void createBookImage(long id, URI location, InputStream inputStream, long size) {
        Book book = bookRepository.findById(id).orElseThrow();
        book.setCoverPicUrl(location.toString());
        book.setCoverPicFile(BlobProxy.generateProxy(inputStream, size));
        bookRepository.save(book);
    }

    public Resource getBookImage(long id) throws SQLException {
        Book book = bookRepository.findById(id).orElseThrow();
        if (book.getCoverPicFile() != null) {
            return new InputStreamResource(book.getCoverPicFile().getBinaryStream());
        } else {
            throw new NoSuchElementException();
        }
    }

    public void replaceBookImage(long id, InputStream inputStream, long size) {
        Book book = bookRepository.findById(id).orElseThrow();
        if (book.getCoverPicUrl() == null) throw new NoSuchElementException();
        book.setCoverPicFile(BlobProxy.generateProxy(inputStream, size));
        bookRepository.save(book);
    }

    public void deleteBookImage(long id) {
        Book book = bookRepository.findById(id).orElseThrow();
        if (book.getCoverPicUrl() == null) throw new NoSuchElementException();
        book.setCoverPicFile(null);
        book.setCoverPicUrl(null);
        bookRepository.save(book);
    }

}
