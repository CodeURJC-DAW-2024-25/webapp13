package es.codeurjc13.librored.dto;

public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String genre;
    private String description;
    private boolean available;
    private String coverPic;
    private String authorBio;
    private String coverEndpoint;
    private Long ownerId;
    private String ownerName;

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public String getAuthorBio() {
        return authorBio;
    }

    public String getCoverEndpoint() {
        return coverEndpoint;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    // SETTERS (necesarios para MapStruct)
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public void setAuthorBio(String authorBio) {
        this.authorBio = authorBio;
    }

    public void setCoverEndpoint(String coverEndpoint) {
        this.coverEndpoint = coverEndpoint;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
