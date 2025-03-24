package es.codeurjc13.librored.dto;

public record BookCreateDTO(
        String title,
        String author,
        String description,
        String genre
) {
}
