package es.codeurjc13.librored.dto;

import es.codeurjc13.librored.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Complete User DTO for REST API operations
 * Password field is optional - only used for user creation
 */
public record UserDTO(
        Long id,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        User.Role role,

        // Optional password field - only used for creation, never returned in responses
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password) {
}