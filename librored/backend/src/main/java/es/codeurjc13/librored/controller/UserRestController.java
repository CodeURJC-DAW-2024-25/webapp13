package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.dto.UserCreateDTO;
import es.codeurjc13.librored.dto.UserDTO;
import es.codeurjc13.librored.dto.UserUpdateDTO;
import es.codeurjc13.librored.mapper.UserMapper;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management API")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserRestController(UserService userService, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // GET ALL
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve a list of all users.")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)))
    public List<UserDTO> getAllUsers() {
        return userService.findAll().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID.")
    @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new user", description = "Register a new user with a username, email and password.")
    @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid user data or email already in use")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO dto) {
        User user = userMapper.toDomain(dto);
        user.setEncodedPassword(passwordEncoder.encode(dto.password()));
        user.setRole(User.Role.ROLE_USER); // Seguridad: nunca se asigna el rol desde el exterior

        User savedUser = userService.save(user);

        URI location = URI.create("/api/users/" + savedUser.getId());
        return ResponseEntity.created(location).body(userMapper.toDTO(savedUser));
    }

    @Operation(summary = "Update user", description = "Update the user's username and/or email.")
    @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        return userService.findById(id)
                .map(user -> {
                    userMapper.updateUserFromDto(dto, user);
                    userService.save(user);
                    return ResponseEntity.ok(userMapper.toDTO(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete user", description = "Delete a user account by their ID.")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Custom endpoints for user management

    @Operation(summary = "Update username", description = "Update the username of the currently logged-in user.")
    @ApiResponse(responseCode = "200", description = "Username updated successfully")
    @ApiResponse(responseCode = "400", description = "New username is empty or invalid")
    @PostMapping("/update-username")
    public ResponseEntity<Map<String, Object>> updateUsername(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestParam String newUsername) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", "User not authenticated."));
        }

        String email = userDetails.getUsername();  // ✅ Always fetch by email
        Optional<User> user = userService.getUserByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", "User not found."));
        }

        System.out.println("🔍 Updating username for: " + user.get().getEmail());
        user.get().setUsername(newUsername);
        userService.saveUser(user.get());

        //  Refresh authentication to ensure future requests still work
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.get(), user.get().getEncodedPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok(Map.of("success", true, "message", "Username updated successfully!"));
    }

    @Operation(summary = "Update password", description = "Change the password of the currently logged-in user.")
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    @ApiResponse(responseCode = "400", description = "Current password is invalid")
    @PostMapping("/update-password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", "User not authenticated."));
        }

        String email = userDetails.getUsername(); //  Always fetch by email
        Optional<User> user = userService.getUserByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", "User not found."));
        }

        if (!passwordEncoder.matches(currentPassword, user.get().getEncodedPassword())) {
            return ResponseEntity.ok(Map.of("success", false, "error", "Incorrect current password."));
        }

        // Encode and update the new password
        user.get().setEncodedPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(user.get());

        // Invalidate session after password change
        SecurityContextHolder.clearContext();

        // Redirect to login page with success message
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password updated successfully! Redirecting to login..."
        ));
    }

    @Operation(summary = "Verify password", description = "Verify the current password of the logged-in user.")
    @ApiResponse(responseCode = "200", description = "Password verification result")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @PostMapping("/verify-password")
    public ResponseEntity<Map<String, Object>> verifyPassword(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestParam String currentPassword) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", "User is not authenticated."));
        }

        String email = userDetails.getUsername(); // ✅ Always fetch by email, NOT username
        Optional<User> user = userService.getUserByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", "User not found."));
        }

        if (!passwordEncoder.matches(currentPassword, user.get().getEncodedPassword())) {
            return ResponseEntity.ok(Map.of("success", false, "error", "Incorrect current password."));
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "Password verified! You can now enter a new password."));
    }


}
