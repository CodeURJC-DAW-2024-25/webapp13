package es.codeurjc13.librored.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")  // ✅ Fix: Ensure login is mapped correctly
public class LoginWebController {


    @GetMapping
    public String login() {
        return "login";
    }

    @RequestMapping("/loginerror")
    public String loginerror() {
        return "loginerror";
    }
}
