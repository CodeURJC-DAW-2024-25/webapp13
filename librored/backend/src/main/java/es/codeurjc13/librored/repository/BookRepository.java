package es.codeurjc13.librored.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc13.librored.model.Book;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b.genre, COUNT(b) FROM Book b GROUP BY b.genre")
    List<Object[]> countBooksByGenre();

    @Query("SELECT b FROM Book b WHERE b.owner.id = :ownerId")
    List<Book> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Book b WHERE b.owner.id = :ownerId AND " +
            "b.id NOT IN (SELECT l.book.id FROM Loan l WHERE l.status = 'Active')")
    List<Book> findAvailableBooksByOwnerId(@Param("ownerId") Long ownerId);



}