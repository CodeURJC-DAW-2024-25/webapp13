package es.codeurjc13.librored.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "FK_loan_book"))
    private Book book;

    @ManyToOne
    @JoinColumn(name = "borrower_id", nullable = false, foreignKey = @ForeignKey(name = "FK_loan_borrower"))
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "lender_id", nullable = false, foreignKey = @ForeignKey(name = "FK_loan_lender"))
    private User lender;


    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        Active, Completed
    }

    public Loan() {
    }

    public Loan(Book book, User lender, User borrower, LocalDate startDate, LocalDate endDate, Status status) {
        this.book = book;
        this.lender = lender;
        this.borrower = borrower;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getLender() {
        return lender;
    }

    public void setLender(User lender) {
        this.lender = lender;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}