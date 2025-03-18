package es.codeurjc13.librored.service;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return bookRepository.save(book);
    }

    // Check if a book exists by ID
    public boolean existsById(Long id) {
        return bookRepository.existsById(id);
    }

    // Delete a book by ID
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    public Page<Book> findAllPage(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
}
