package es.codeurjc13.librored.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc13.librored.model.Book;

public interface UserRepository extends JpaRepository<Book, Long> {
}