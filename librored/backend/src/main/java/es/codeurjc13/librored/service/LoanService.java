package es.codeurjc13.librored.service;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public void saveLoan(Loan loan) {
        loanRepository.save(loan);
    }

    public void updateLoan(Long id, Loan updatedLoan) {
        Optional<Loan> existingLoan = loanRepository.findById(id);
        if (existingLoan.isPresent()) {
            Loan loan = existingLoan.get();

            // Update book, lender, borrower, dates, and status
            if (updatedLoan.getBook() != null) {
                loan.setBook(updatedLoan.getBook());
            }
            if (updatedLoan.getLender() != null) {
                loan.setLender(updatedLoan.getLender());
            }
            if (updatedLoan.getBorrower() != null) {
                loan.setBorrower(updatedLoan.getBorrower());
            }
            if (updatedLoan.getStartDate() != null) {
                loan.setStartDate(updatedLoan.getStartDate());
            }
            if (updatedLoan.getEndDate() != null) {
                loan.setEndDate(updatedLoan.getEndDate());
            }
            if (updatedLoan.getStatus() != null) {
                loan.setStatus(updatedLoan.getStatus());
            }

            loanRepository.save(loan);
        }
    }

    public void createLoan(Book book, User lender, User borrower, LocalDate startDate, LocalDate endDate, Loan.Status status) {
        Loan loan = new Loan(book, lender, borrower, startDate, endDate, status);
        loanRepository.save(loan);
    }


    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }
}

