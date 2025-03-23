package es.codeurjc13.librored.dto;

public record BookDTO(
        Long id,
        String title,
        String description,
        String genre,
        String author,
        boolean available,
        String coverEndpoint,
        Long ownerId,
        String ownerName
) {}
