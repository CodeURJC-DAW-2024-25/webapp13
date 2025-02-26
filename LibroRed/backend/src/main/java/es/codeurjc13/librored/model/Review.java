package es.codeurjc13.librored.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Book book;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDate review_date = LocalDate.now();

    public enum Rating {
        ONE, TWO, THREE, FOUR, FIVE
    }

    public Review() {}

    public Review(Book book, User user, Rating rating, String comment) {
        this.book = book;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.review_date = LocalDate.now();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getReview_date() {
        return review_date;
    }

    public void setReview_date(LocalDate review_date) {
        this.review_date = review_date;
    }

}
