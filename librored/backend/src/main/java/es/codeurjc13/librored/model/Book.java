package es.codeurjc13.librored.model;
import java.sql.Blob;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;

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
    @JsonIgnore // ✅ Ignore this field when serializing to JSON
    private Blob cover_pic;

    private boolean image;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    private User owner;

    public enum Genre {
        Fiction, Non_Fiction, Mystery_Thriller, SciFi_Fantasy, Romance, Historical_Fiction, Horror
    }

    public Book() {}

    public Book(String title, String author, Genre genre, String description, User owner) {
        super();
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
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

    public Blob getCover_picFile() {
        return cover_pic;
    }

    public void setCover_picFile(Blob cover_pic) {
        this.cover_pic = cover_pic;
    }

    public boolean getCover(){
        return this.image;
    }

    public void setCover(boolean image){
        this.image = image;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }


    public String getCoverPicBase64() {
        if (cover_pic == null) {
            return null;
        }
        try {
            byte[] bytes = cover_pic.getBytes(1, (int) cover_pic.length());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setCoverPicBase64(String base64) {
        // ✅ Convert Base64 to Blob
        if (base64 != null) {
            byte[] bytes = Base64.getDecoder().decode(base64);
            try {
                this.cover_pic = new javax.sql.rowset.serial.SerialBlob(bytes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
