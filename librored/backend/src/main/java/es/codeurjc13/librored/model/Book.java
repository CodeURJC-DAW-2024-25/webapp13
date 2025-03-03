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

    @Lob  // ✅ Store image as BLOB (Binary Large Object)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] cover_pic;

    @Column(columnDefinition = "TEXT")
    private String description;


    @ManyToOne
    private User owner;

    public enum Genre {
        Fiction, Non_Fiction, Mystery_Thriller, SciFi_Fantasy, Romance, Historical_Fiction, Horror
    }

    public Book() {}

    public Book(String title, String author, Genre genre, String description, byte[] cover_pic, User owner) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.cover_pic = cover_pic;
        this.owner = owner;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getCover_pic() {
        return cover_pic;
    }

    public void setCover_pic(byte[] cover_pic) {
        this.cover_pic = cover_pic;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
