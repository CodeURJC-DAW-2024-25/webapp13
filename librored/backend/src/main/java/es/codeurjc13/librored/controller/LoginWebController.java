package es.codeurjc13.librored.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class LoginWebController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("csrf_token", csrfToken.getToken());
        model.addAttribute("csrf_parameter", csrfToken.getParameterName());
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