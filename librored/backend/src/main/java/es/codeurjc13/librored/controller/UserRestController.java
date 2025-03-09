package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserRestController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

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
            System.out.println("🔴 User not found in the database!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", "User not found."));
        }

        System.out.println("🔍 Updating username for: " + user.get().getEmail());
        user.get().setUsername(newUsername);
        userService.saveUser(user.get());

        // ✅ Refresh authentication to ensure future requests still work
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.get(), user.get().getEncodedPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok(Map.of("success", true, "message", "Username updated successfully!"));
    }



    @PostMapping("/update-password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", "User not authenticated."));
        }

        String email = userDetails.getUsername(); // ✅ Always fetch by email
        Optional<User> user = userService.getUserByEmail(email);

        if (user.isEmpty()) {
            System.out.println("🔴 User not found in the database!");
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




    @PostMapping("/verify-password")
    public ResponseEntity<Map<String, Object>> verifyPassword(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestParam String currentPassword) {

        if (userDetails == null) {
            System.out.println("🔴 User is not authenticated!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", "User is not authenticated."));
        }

        String email = userDetails.getUsername(); // ✅ Always fetch by email, NOT username
        Optional<User> user = userService.getUserByEmail(email);

        if (user.isEmpty()) {
            System.out.println("🔴 User not found in the database!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", "User not found."));
        }

        System.out.println("🔍 Verifying password for: " + user.get().getEmail());

        if (!passwordEncoder.matches(currentPassword, user.get().getEncodedPassword())) {
            return ResponseEntity.ok(Map.of("success", false, "error", "Incorrect current password."));
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "Password verified! You can now enter a new password."));
    }



}
