package es.codeurjc13.librored.repository;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByLender(User lender);

    @Query("SELECT l FROM Loan l WHERE l.borrower = :borrower")
    List<Loan> findByBorrower(@Param("borrower") User borrower);

    @Query("SELECT l FROM Loan l WHERE l.book = :book")
    List<Loan> findByBook(@Param("book") Book book);

    @Query("SELECT l FROM Loan l WHERE l.borrower = :user OR l.lender = :user")
    List<Loan> findByBorrowerOrLender(@Param("user") User user);

}
