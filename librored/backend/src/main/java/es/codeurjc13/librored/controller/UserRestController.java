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

        String username = userDetails.getUsername(); // ✅ Use username
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found in the database!"));

        user.setUsername(newUsername);
        userService.saveUser(user);

        // Update authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getEncodedPassword(), userDetails.getAuthorities());
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

        String username = userDetails.getUsername(); // ✅ Use username, not email
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found in the database!"));

        if (!passwordEncoder.matches(currentPassword, user.getEncodedPassword())) {
            return ResponseEntity.ok(Map.of("success", false, "error", "Incorrect current password."));
        }

        // Encode and update the new password
        user.setEncodedPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(user);

        // Update authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, newPassword, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok(Map.of("success", true, "message", "Password updated successfully!"));
    }





    @PostMapping(value = "/verify-password", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, Object>> verifyPassword(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestParam String currentPassword) {

        if (userDetails == null) {
            System.out.println("🔴 User is not authenticated!");
            return ResponseEntity.ok(Map.of("success", false, "error", "User is not authenticated."));
        }

        System.out.println("✅ Authenticated user: " + userDetails.getUsername());

        Optional<User> user = userService.getUserByUsername(userDetails.getUsername());

        if (user.isEmpty()) {
            System.out.println("🔴 User not found in the database!");
            return ResponseEntity.ok(Map.of("success", false, "error", "User not found."));
        }

        System.out.println("🔍 Verifying password for: " + user.get().getEmail());

        if (!passwordEncoder.matches(currentPassword, user.get().getEncodedPassword())) {
            return ResponseEntity.ok(Map.of("success", false, "error", "Incorrect current password."));
        }

        System.out.println("✅ Password verified successfully!");

        return ResponseEntity.ok(Map.of("success", true, "message", "Password verified! You can now enter a new password."));
    }



}
