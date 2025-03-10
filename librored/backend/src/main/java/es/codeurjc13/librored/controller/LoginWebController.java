package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginWebController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "successMessage", required = false) String successMessage,
            Model model) {

        if (error != null) {
            model.addAttribute("loginError", "Invalid username or password.");
        }

        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }

        return "login"; // ✅ Ensure login.html displays the message
    }


    // Muestra el formulario de registro
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Correspondiente a register.html
    }

    // Procesa el registro de un nuevo usuario
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.registerUser(user);
        return "redirect:/login?registered";
    }
}
