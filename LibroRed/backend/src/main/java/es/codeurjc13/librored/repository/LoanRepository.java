package es.codeurjc13.librored.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc13.librored.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
