package es.codeurjc13.librored.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Book book;

    @ManyToOne
    private User lender;

    @ManyToOne
    private User borrower;

    private Date startDate;
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        Active, Completed
    }

    public Loan() {
    }

    public Loan(Book book, User lender, User borrower, Date startDate, Date endDate, Status status) {
        this.book = book;
        this.lender = lender;
        this.borrower = borrower;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and setters
}