package es.codeurjc13.librored.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PostLoginController {

    private static final Logger logger = LoggerFactory.getLogger(PostLoginController.class);

    @GetMapping("/post-login")
    public String postLogin(HttpServletRequest request, Model model) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            model.addAttribute("csrf_token", csrfToken.getToken());
            model.addAttribute("csrf_parameter", csrfToken.getParameterName());

            // 🔴 Log CSRF token after successful login
            logger.info("Post-Login Page - CSRF Token: {}", csrfToken.getToken());
        } else {
            model.addAttribute("csrf_token", "MISSING");
            model.addAttribute("csrf_parameter", "_csrf");

            logger.warn("Post-Login Page - CSRF Token is MISSING!");
        }

        return "index"; // Redirect back to the home page
    }
}
