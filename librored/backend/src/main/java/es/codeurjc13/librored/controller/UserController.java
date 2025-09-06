package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final BookService bookService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, BookService bookService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.bookService = bookService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            User currentUser = user.get();
            model.addAttribute("user", currentUser);

            // Add role selection flags for template
            model.addAttribute("isUserRole", currentUser.getRole() == User.Role.ROLE_USER);
            model.addAttribute("isAdminRole", currentUser.getRole() == User.Role.ROLE_ADMIN);

            return "edit-user";
        }
        return "redirect:/users";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) String role,
                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User loggedUser) {

        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = loggedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(User.Role.ROLE_ADMIN.name()));

        // Users can update only their own account
        if (!isAdmin && !user.getEmail().equals(loggedUser.getUsername())) {
            return "redirect:/users"; // Redirect unauthorized users
        }

        user.setUsername(username);

        if (isAdmin) {
            if (email != null) user.setEmail(email);
            if (role != null) {
                String formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                user.setRole(User.Role.valueOf(formattedRole));
            }
        }

        if (password != null && !password.isEmpty()) {
            user.setEncodedPassword(passwordEncoder.encode(password));
        }

        userService.saveUser(user);

        return "redirect:/users";
    }


    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "create-user";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user) {
        userService.registerUser(user);  // Ensure password encoding
        return "redirect:/users";
    }

    @GetMapping("/myaccount")
    public String myAccount(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.getUserByEmail(userDetails.getUsername());

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();
        
        // Fetch recommended books for the user
        List<Book> recommendedBooks = bookService.getRecommendationsForUser(user.getId());

        // Pass user and recommendations to the template
        model.addAttribute("user", user);
        model.addAttribute("recommendedBooks", recommendedBooks);
        model.addAttribute("logged", true);

        return "myaccount";
    }


}
