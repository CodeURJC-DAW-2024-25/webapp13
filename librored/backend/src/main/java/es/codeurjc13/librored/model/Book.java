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

    @Column(length = 2000)
    private String authorBio;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @JsonIgnore // This prevents serialization issues
    private Blob coverPic;

    private boolean available = true;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "FK_book_owner"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevent infinite recursion in JSON serialization
    private List<Loan> loans;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    public enum Genre {
        Fiction, Non_Fiction, Mystery_Thriller, SciFi_Fantasy, Romance, Historical_Fiction, Horror
    }

    // CONSTRUCTORS
    public Book() {
    }

    public Book(String title, String author, Genre genre, String description, User owner) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.coverPic = null; // Will be filled in when image is uploaded
        this.owner = owner;
    }

    // GETTERS AND SETTERS
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

    public String getAuthorBio() { return authorBio; }

    public void setAuthorBio(String authorBio) { this.authorBio = authorBio; }

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

    public boolean isAvailable() {
        return this.available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // OWNER (it is a user) GETTERS AND SETTERS
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    // LOANs relationship
    public boolean isCurrentlyOnLoan() {
        if (this.loans == null || this.loans.isEmpty()) {
            return false;
        }
        // Check if any loan is still active
        return this.loans.stream().anyMatch(loan -> loan.getStatus() == Loan.Status.Active);
    }

}