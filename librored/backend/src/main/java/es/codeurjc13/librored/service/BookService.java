package es.codeurjc13.librored.service;

import es.codeurjc13.librored.dto.BookDTO;
import es.codeurjc13.librored.dto.BookBasicDTO;
import es.codeurjc13.librored.mapper.BookMapper;
import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.BookRepository;
import es.codeurjc13.librored.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

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

    // ==================== DTO-BASED METHODS FOR REST API ====================

    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooksDTO() {
        List<Book> books = bookRepository.findAll();
        return bookMapper.toDTOs(books);
    }

    public Optional<BookDTO> getBookByIdDTO(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(bookMapper::toDTO);
    }

    public BookDTO createBookDTO(BookDTO bookDTO) {
        Book book = bookMapper.toDomain(bookDTO);
        
        // Set the owner from the DTO
        if (bookDTO.owner() != null) {
            Optional<User> owner = userRepository.findById(bookDTO.owner().id());
            if (owner.isPresent()) {
                book.setOwner(owner.get());
            } else {
                throw new IllegalArgumentException("Owner not found with id: " + bookDTO.owner().id());
            }
        }
        
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDTO(savedBook);
    }

    public Optional<BookDTO> updateBookDTO(Long id, BookDTO bookDTO) {
        Optional<Book> existingBookOpt = bookRepository.findById(id);
        if (existingBookOpt.isPresent()) {
            Book book = existingBookOpt.get();
            book.setTitle(bookDTO.title());
            book.setAuthor(bookDTO.author());
            book.setGenre(bookDTO.genre());
            book.setDescription(bookDTO.description());
            
            // Update owner if provided
            if (bookDTO.owner() != null) {
                Optional<User> owner = userRepository.findById(bookDTO.owner().id());
                if (owner.isPresent()) {
                    book.setOwner(owner.get());
                } else {
                    throw new IllegalArgumentException("Owner not found with id: " + bookDTO.owner().id());
                }
            }
            
            Book savedBook = bookRepository.save(book);
            return Optional.of(bookMapper.toDTO(savedBook));
        }
        return Optional.empty();
    }

    public boolean deleteBookDTO(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<BookDTO> getBooksByOwnerIdDTO(Long ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isPresent()) {
            List<Book> books = bookRepository.findByOwner(owner.get());
            return bookMapper.toDTOs(books);
        }
        return List.of();
    }

    public List<BookBasicDTO> getAvailableBooksByOwnerIdDTO(Long ownerId) {
        List<Book> books = bookRepository.findAvailableBooksByOwnerId(ownerId);
        return bookMapper.toBasicDTOs(books);
    }

    public Map<String, Long> getBooksPerGenreDTO() {
        List<Object[]> results = bookRepository.countBooksByGenre();
        Map<String, Long> booksPerGenre = new HashMap<>();

        for (Object[] result : results) {
            Book.Genre genreEnum = (Book.Genre) result[0];
            String genre = genreEnum.name();
            Long count = (Long) result[1];
            booksPerGenre.put(genre, count);
        }

        return booksPerGenre;
    }

    public List<BookDTO> getRecommendationsForUserDTO(Long userId) {
        List<Book> books = bookRepository.findRecommendedBooks(userId);
        return bookMapper.toDTOs(books);
    }

}
