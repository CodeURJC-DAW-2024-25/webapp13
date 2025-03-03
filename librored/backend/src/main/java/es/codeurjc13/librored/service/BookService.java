package es.codeurjc13.librored.service;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Book> getBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable).getContent();
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

    public void updateBook(Long id, Book updatedBook) {
        Optional<Book> existingBookOpt = bookRepository.findById(id);

        if (existingBookOpt.isPresent()) {
            Book existingBook = existingBookOpt.get();

            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setCover_pic(updatedBook.getCover_pic());
            existingBook.setDescription(updatedBook.getDescription());
            existingBook.setGenre(updatedBook.getGenre());
            existingBook.setOwner(updatedBook.getOwner());

            bookRepository.save(existingBook);
        }
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

    public void createBook(String title, String author, String description, String coverPic, Book.Genre genre, User owner) {
        Book book = new Book(title, author, genre, description, coverPic, owner);
        bookRepository.save(book);
    }


    public List<Book> getBooksByOwnerId(Long ownerId) {
        return bookRepository.findByOwnerId(ownerId);
    }

    public List<Book> getAvailableBooksByOwnerId(Long ownerId) {
        return bookRepository.findAvailableBooksByOwnerId(ownerId);
    }


    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

}
