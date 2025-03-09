package es.codeurjc13.librored.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Blob;
import java.util.List;

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
    //private String coverPic;


    @Lob
    @JsonIgnore // 👈 This prevents serialization issues
    private Blob coverPic;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "FK_book_owner"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevent infinite recursion in JSON serialization
    private List<Loan> loans;


    public enum Genre {
        Fiction, Non_Fiction, Mystery_Thriller, SciFi_Fantasy, Romance, Historical_Fiction, Horror
    }

    public Book() {
    }

    public Book(String title, String author, Genre genre, String description, User owner) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.coverPic = null;
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
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Blob getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(Blob coverPic) {
        this.coverPic = coverPic;
    }

    public String getCoverPicUrl() {
        if (this.coverPic != null) {
            return "/books/" + this.id + "/image";
        } else {
            return "/images/default_cover.jpg"; // Return default image when cover is null
        }
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
