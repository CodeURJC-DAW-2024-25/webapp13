package es.codeurjc13.librored.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc13.librored.model.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b.genre, COUNT(b) FROM Book b GROUP BY b.genre")
    List<Object[]> countBooksByGenre();




}