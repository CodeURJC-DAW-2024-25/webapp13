package es.codeurjc13.librored.dto;

public record UserCreateDTO(
        String username,
        String email,
        String rawPassword
) {}
