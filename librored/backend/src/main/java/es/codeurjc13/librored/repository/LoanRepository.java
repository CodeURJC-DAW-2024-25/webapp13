package es.codeurjc13.librored.repository;

import es.codeurjc13.librored.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
