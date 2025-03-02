package es.codeurjc13.librored.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")  // ✅ Fix: Ensure login is mapped correctly
public class LoginWebController {

    private static final Logger logger = LoggerFactory.getLogger(LoginWebController.class);

    @GetMapping
    public String login(HttpServletRequest request, Model model) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            request.getSession().setAttribute("SESSION_CSRF_TOKEN", csrfToken.getToken()); // 🔴 Store CSRF token in session
            model.addAttribute("csrf_token", csrfToken.getToken());
            model.addAttribute("csrf_parameter", csrfToken.getParameterName());
        } else {
            model.addAttribute("csrf_token", request.getSession().getAttribute("SESSION_CSRF_TOKEN")); // 🔴 Retrieve existing token
            model.addAttribute("csrf_parameter", "_csrf");
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
