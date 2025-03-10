package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final BookService bookService; // Inject BookService to fetch recommendations

    public UserController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
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
            model.addAttribute("user", user.get());
            return "edit-user";
        }
        return "redirect:/users";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User updatedUser) {
        userService.updateUser(id, updatedUser);
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
            System.out.println("🔴 User is not authenticated!");
            return "redirect:/login";
        }

        System.out.println("✅ Authenticated user: " + userDetails.getUsername());

        Optional<User> user = userService.getUserByEmail(userDetails.getUsername());

        if (user.isEmpty()) {
            System.out.println("🔴 User not found in the database!");
            return "redirect:/login";
        }

        // Fetch recommended books for the user
        List<Book> recommendedBooks = bookService.getRecommendationsForUser(user.get().getId());

        // Pass user and recommendations to the template
        model.addAttribute("user", user.get());
        model.addAttribute("recommendedBooks", recommendedBooks);
        model.addAttribute("logged", true);

        return "myaccount";
    }


}
