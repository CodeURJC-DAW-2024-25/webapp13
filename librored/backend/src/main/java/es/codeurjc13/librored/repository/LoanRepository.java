package es.codeurjc13.librored.repository;

import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByLender(User lender);

}
