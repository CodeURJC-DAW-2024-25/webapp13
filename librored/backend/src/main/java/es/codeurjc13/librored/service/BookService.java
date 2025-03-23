package es.codeurjc13.librored.service;

import es.codeurjc13.librored.dto.BookCreateDTO;
import es.codeurjc13.librored.mapper.BookMapper;
import es.codeurjc13.librored.dto.BookUpdateDTO;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

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

    public Page<Book> searchBooks(String title, String author, String genre, Pageable pageable) {
        return bookRepository.searchBooks((title != null && !title.isEmpty()) ? title : null, (author != null && !author.isEmpty()) ? author : null, (genre != null && !genre.isEmpty()) ? genre : null, pageable);
    }

    // Image Endpoint related methods
    public void createBookImage(long id, InputStream inputStream, long size) {
        Book book = bookRepository.findById(id).orElseThrow();
        book.setCoverPic(BlobProxy.generateProxy(inputStream, size));
        bookRepository.save(book);
    }

    public Resource getBookImage(long id) throws SQLException {
        Book book = bookRepository.findById(id).orElseThrow();

        if (book.getCoverPic() != null) {
            return new InputStreamResource(book.getCoverPic().getBinaryStream());
        } else {
            throw new NoSuchElementException();
        }
    }

    public void replaceBookImage(long id, InputStream inputStream, long size) {
        Book book = bookRepository.findById(id).orElseThrow();

        if (book.getCoverPic() == null) {
            throw new NoSuchElementException();
        }

        book.setCoverPic(BlobProxy.generateProxy(inputStream, size));
        bookRepository.save(book);
    }

    public void deleteBookImage(long id) {
        Book book = bookRepository.findById(id).orElseThrow();

        if (book.getCoverPic() == null) {
            throw new NoSuchElementException();
        }

        book.setCoverPic(null);
        bookRepository.save(book);
    }

    public boolean isOwnerOrAdmin(Long bookId, String userEmail, Collection<? extends GrantedAuthority> authorities) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            return false;
        }

        Book book = bookOpt.get();
        boolean isAdmin = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || book.getOwner().getEmail().equals(userEmail);
    }

    public Book createBook(BookCreateDTO dto, User owner) {
        Book book = bookMapper.toDomain(dto);
        book.setOwner(owner); // asignar manualmente el owner
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, BookUpdateDTO dto) {
        Book book = bookRepository.findById(id).orElseThrow();
        Book updated = bookMapper.toDomain(dto);
        updated.setId(id);
        updated.setOwner(book.getOwner()); // mantener el mismo owner
        updated.setCoverPic(book.getCoverPic()); // mantener imagen
        return bookRepository.save(updated);
    }

}
