package es.codeurjc13.librored.dto;

public record BookUpdateDTO(
        String title,
        String author,
        String description,
        String genre,
        boolean available
) {
}
