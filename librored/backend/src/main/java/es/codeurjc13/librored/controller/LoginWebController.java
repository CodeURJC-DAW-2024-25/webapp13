package es.codeurjc13.librored.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/login")  // ✅ Fix: Ensure login is mapped correctly
public class LoginWebController {

    private static final Logger logger = LoggerFactory.getLogger(LoginWebController.class);

    @GetMapping
    public String login(HttpServletRequest request, Model model) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            model.addAttribute("csrf_token", csrfToken.getToken());
            model.addAttribute("csrf_parameter", csrfToken.getParameterName());

            // ✅ Fix: Log CSRF token every time login page is accessed
            logger.info("CSRF Token Retrieved for Login Page: {}", csrfToken.getToken());
        } else {
            model.addAttribute("csrf_token", "MISSING");
            model.addAttribute("csrf_parameter", "_csrf");

            // ✅ Fix: Use warn() instead of warning()
            logger.warn("CSRF Token is MISSING when loading login page!");
        }

        return "login";
    }

    @RequestMapping("/loginerror")
    public String loginerror(HttpServletRequest request, Model model) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("csrf_token", csrfToken.getToken());
        model.addAttribute("csrf_parameter", csrfToken.getParameterName());
        return "loginerror";
    }
}
