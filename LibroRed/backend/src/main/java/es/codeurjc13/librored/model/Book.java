package es.codeurjc13.librored.model;

import jakarta.persistence.*;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String author;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String cover_pic;

    @ManyToOne
    private User owner;

    public enum Genre {
        Fiction, Non_Fiction, Mystery_Thriller, SciFi_Fantasy, Romance, Historical_Fiction, Horror
    }

    public Book() {}

    public Book(String title, String author, Genre genre, String description, String cover_pic, User owner) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.cover_pic = cover_pic;
        this.owner = owner;
    }

    // Getters and setters
}
