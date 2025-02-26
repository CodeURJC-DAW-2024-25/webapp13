package es.codeurjc13.librored.model;

import jakarta.persistence.*;
import java.util.Date;

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

    @Temporal(TemporalType.TIMESTAMP)
    private Date review_date = new Date();

    public enum Rating {
        ONE, TWO, THREE, FOUR, FIVE
    }

    public Review() {}

    public Review(Book book, User user, Rating rating, String comment) {
        this.book = book;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.review_date = new Date();
    }

    // Getters and setters
}
